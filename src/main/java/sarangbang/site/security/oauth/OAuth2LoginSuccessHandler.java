package sarangbang.site.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
import sarangbang.site.auth.service.RefreshTokenService;
import sarangbang.site.security.jwt.JwtTokenProvider;
import sarangbang.site.security.oauth.userinfo.GoogleUserInfo;
import sarangbang.site.security.oauth.userinfo.KakaoUserInfo;
import sarangbang.site.security.oauth.userinfo.NaverUserInfo;
import sarangbang.site.security.oauth.userinfo.OAuth2UserInfo;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Value("${oauth2.success.redirect.uri}")
    private String successRedirectUri;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        switch (registrationId) {
            case "google" -> oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
            case "naver" ->
                    oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
            case "kakao" -> oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
            default -> {
                log.error("Unsupported provider: {}", registrationId);
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported provider");
                return;
            }
        }

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String email = oAuth2UserInfo.getEmail();

        User user = userRepository.findByProviderAndProviderId(provider, providerId)
                .orElseGet(() -> {
                    User newUser = new User(
                            UUID.randomUUID().toString(),
                            email,
                            null, // password
                            oAuth2UserInfo.getName(), // nickname
                            null, // gender
                            null, // region
                            null,
                            provider,
                            providerId,
                            false // profileComplete
                    );
                    return userRepository.save(newUser);
                });

        String accessToken = jwtTokenProvider.createAccessToken(user.getId(), user.getEmail(), Collections.singletonList("ROLE_USER"));
        
        // AuthController와 동일하게 RefreshToken 생성 및 쿠키 설정
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId());
        refreshTokenService.saveToken(user.getId(), refreshToken);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60)
                .sameSite("None")
                .build();
        response.addHeader("Set-Cookie", cookie.toString());

        // AuthController 로그인과 동일한 사용자 정보를 쿼리 파라미터에 포함
        // 한국어 닉네임 등의 특수문자가 포함될 수 있으므로 인코딩 처리
        String targetUrl = UriComponentsBuilder.fromUriString(successRedirectUri)
                .queryParam("accessToken", accessToken)
                .queryParam("uuid", user.getId())
                .queryParam("nickname", user.getNickname())
                .queryParam("profileImageUrl", user.getProfileImageUrl())
                .queryParam("profileComplete", user.isProfileComplete())
                .encode() // 명시적으로 URL 인코딩 처리
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
} 