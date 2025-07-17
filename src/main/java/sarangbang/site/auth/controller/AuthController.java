package sarangbang.site.auth.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.auth.dto.SignInRequestDTO;
import sarangbang.site.auth.exception.EmailAlreadyExistsException;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.auth.service.AuthService;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.security.jwt.JwtTokenProvider;
import sarangbang.site.auth.dto.SignupRequestDTO;

import java.net.URI;
import java.util.Map;

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
            @ApiResponse(responseCode = "200", description = "로그인 성공", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"token\": \"eyJhbGciOiJIUzI1NiJ9...\", \"message\": \"로그인 성공\"}"))),
            @ApiResponse(responseCode = "401", description = "로그인 실패", content = @Content(mediaType = "application/json", schema = @Schema(example = "{\"error\": \"이메일 또는 비밀번호가 일치하지 않습니다.\"}")))
    })
    @PostMapping("/signin")
    public ResponseEntity<Map<String, String>> login(@RequestBody SignInRequestDTO request) {
        try{
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

            Map<String, String> responseBody = Map.of(
                    "token", token,
                    "message", "로그인 성공"
            );
            return ResponseEntity.ok().header("Authorization", "Bearer " + token).body(responseBody);
        } catch (AuthenticationException e){
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
    public ResponseEntity<?> register(@Valid @RequestBody SignupRequestDTO requestDto) {
        try{
            String userId = authService.register(requestDto);
            Map<String, String> responseBody = Map.of(
                    "userId", userId,
                    "message", "회원가입이 성공적으로 완료되었습니다."
            );
            return ResponseEntity.created(URI.create("/api/users/" + userId)).body(responseBody);
        } catch (IllegalArgumentException e){
            Map<String, String> errorBody = Map.of("error", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (EmailAlreadyExistsException e){
            Map<String, String> errorBody = Map.of("email", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (NicknameAlreadyExistsException e) {
            Map<String, String> errorBody = Map.of("nickname", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.CONFLICT);
        } catch (RegionNotFoundException e){
            Map<String, String> errorBody = Map.of("region", e.getMessage());
            return new ResponseEntity<>(errorBody, HttpStatus.NOT_FOUND);
        }
    }
}
