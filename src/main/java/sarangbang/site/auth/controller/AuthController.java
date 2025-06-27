package sarangbang.site.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.auth.dto.SignInRequestDTO;
import sarangbang.site.auth.service.AuthService;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.security.jwt.JwtTokenProvider;
import sarangbang.site.user.dto.SignupRequestDTO;

import java.net.URI;

@Tag(name = "Auth", description = "인증/인가 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthService authService;

    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인을 진행하고 Access토큰을 발급합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공"),
            @ApiResponse(responseCode = "401", description = "로그인 실패")
    })
    @PostMapping("/signin")
    public ResponseEntity<Void> login(@RequestBody SignInRequestDTO request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        CustomUserDetails user = (CustomUserDetails) authentication.getPrincipal();

        String token = jwtTokenProvider.createToken(
                user.getId(),           // UUID
                user.getEmail(),        // email
                user.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList()
        );
        return ResponseEntity.ok().header("Authorization", "Bearer " + token).build();
    }

    @Operation(summary = "회원 가입", description = "사용자 정보를 받아 회원가입을 진행합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "회원가입 성공"),
            @ApiResponse(responseCode = "400", description = "회원가입 실패")
    })
    @PostMapping("/signup")
    public ResponseEntity<Void> register(@Valid @RequestBody SignupRequestDTO requestDto) {
        String userId = authService.register(requestDto);
        return ResponseEntity.created(URI.create("/api/users/" + userId)).build();
    }
}