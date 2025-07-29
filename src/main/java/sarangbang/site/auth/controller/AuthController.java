package sarangbang.site.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.auth.dto.LoginResponseDto;
import sarangbang.site.auth.dto.SignInRequestDTO;
import sarangbang.site.auth.exception.EmailAlreadyExistsException;
import sarangbang.site.auth.exception.InvalidRefreshTokenException;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.auth.service.AuthService;
import sarangbang.site.auth.service.RefreshTokenService;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.security.jwt.JwtTokenProvider;
import sarangbang.site.auth.dto.SignupRequestDTO;
import sarangbang.site.user.entity.User;

import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 Access Token을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(implementation = LoginResponseDto.class))),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"이메일 또는 비밀번호가 일치하지 않습니다.\"}")))
    })
    @PostMapping("/signin")
    public ResponseEntity<?> login(@RequestBody SignInRequestDTO request, HttpServletRequest httpServletRequest, HttpServletResponse response) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            User user = userDetails.getUser();

            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();

            String accessToken = jwtTokenProvider.createAccessToken(
                    userDetails.getId(),           // UUID
                    userDetails.getEmail(),
                    roles
            );

            String refreshTokenValue = jwtTokenProvider.createRefreshToken(
                    userDetails.getId()
            );

            String ipAddress = httpServletRequest.getRemoteAddr();

            long refreshTokenValidity = 14 * 24 * 60 * 60 * 1000L; // 14일 (Provider와 동일하게)
            LocalDateTime expiresAt = new Date(System.currentTimeMillis() + refreshTokenValidity).toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            refreshTokenService.issueNewToken(
                    user,
                    refreshTokenValue,
                    ipAddress,
                    expiresAt
            );

            ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshTokenValue)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .maxAge(7 * 24 * 60 * 60)
                    .sameSite("None")
                    .build();
            response.addHeader("Set-Cookie", cookie.toString());

            LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                    .uuid(userDetails.getId())
                    .nickname(userDetails.getNickname())
                    .profileImageUrl(userDetails.getProfileImageUrl())
                    .accessToken(accessToken)
                    .build();

            return ResponseEntity.ok().header("Authorization", "Bearer " + accessToken).body(loginResponseDto);
        } catch (AuthenticationException e) {
            Map<String, String> errorBody = Map.of("error", "이메일 또는 비밀번호가 올바르지 않습니다.");
            return new ResponseEntity<>(errorBody, HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "회원 가입", description = "사용자 정보를 받아 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"email\": \"testuser2@example.com\", \"message\": \"회원가입이 성공적으로 완료되었습니다.\"}"))),
            @ApiResponse(responseCode = "400", description = "회원가입 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"비밀번호가 일치하지 않습니다.\"}"))),
            @ApiResponse(responseCode = "409", description = "데이터 중복 (이미 사용 중인 이메일 또는 닉네임)", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"이미 가입된 이메일입니다.\"}")))
    })
    @PostMapping("/signup")
    public ResponseEntity<?> register(@Valid @ModelAttribute SignupRequestDTO requestDto) {
        try {
            String userId = authService.register(requestDto);
            Map<String, String> responseBody = Map.of(
                    "userId", userId,
                    "message", "회원가입이 성공적으로 완료되었습니다."
            );
            return ResponseEntity.created(URI.create("/api/users/" + userId)).body(responseBody);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorBody = Map.of("error", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (EmailAlreadyExistsException e) {
            Map<String, String> errorBody = Map.of("email", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (NicknameAlreadyExistsException e) {
            Map<String, String> errorBody = Map.of("nickname", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (RegionNotFoundException e) {
            Map<String, String> errorBody = Map.of("region", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "토큰 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Access Token을 발급합니다.")
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue(name = "refreshToken", required = false) String refreshToken) {
        try {
            String newAccessToken = authService.refresh(refreshToken);
            return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
        } catch (IllegalArgumentException | InvalidRefreshTokenException e) {
            return new ResponseEntity<>(Map.of("error", e.getMessage()), HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        authService.logout(refreshToken);

        ResponseCookie expiredCookie = ResponseCookie.from("refreshToken", "")
                .maxAge(0)
                .path("/")
                .build();
        response.addHeader("Set-Cookie", expiredCookie.toString());

        return ResponseEntity.ok(Map.of("message", "성공적으로 로그아웃되었습니다."));
    }
}