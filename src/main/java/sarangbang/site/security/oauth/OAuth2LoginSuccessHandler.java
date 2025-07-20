package sarangbang.site.security.oauth;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;
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

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();

        OAuth2UserInfo oAuth2UserInfo;
        if (registrationId.equals("google")) {
            oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
        } else if (registrationId.equals("naver")) {
            oAuth2UserInfo = new NaverUserInfo((Map<String, Object>) oAuth2User.getAttributes().get("response"));
        } else if (registrationId.equals("kakao")) {
            oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
        } else {
            log.error("Unsupported provider: {}", registrationId);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unsupported provider");
            return;
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
                            oAuth2UserInfo.getProfileImageUrl(),
                            provider,
                            providerId,
                            false // profileComplete
                    );
                    return userRepository.save(newUser);
                });

        String accessToken = jwtTokenProvider.createToken(user.getId(), user.getEmail(), Collections.singletonList("ROLE_USER"));

        String targetUrl = UriComponentsBuilder.fromUriString("http://localhost:3000/auth/success")
                .queryParam("accessToken", accessToken)
                .queryParam("profileComplete", user.isProfileComplete())
                .build().toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
} 