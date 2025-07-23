package sarangbang.site.user.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.user.dto.UserProfileResponseDTO;
import sarangbang.site.user.dto.UserUpdateNicknameRequestDTO;
import sarangbang.site.user.dto.UserUpdatePasswordRequestDTO;
import sarangbang.site.user.dto.UserUpdateRequestDto;
import sarangbang.site.user.service.UserService;

import java.sql.SQLException;
import java.util.Map;


@Tag(name = "User", description = "유저 관련 API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PatchMapping("/me")
    public ResponseEntity<?> updateMyProfile(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateRequestDto updateDto) {
        try{
            userService.updateUserProfile(userDetails.getId(), updateDto);
        } catch (RegionNotFoundException | NicknameAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    // 로그인 된 사용자 정보 확인
    @Operation(summary = "사용자 정보 확인", description = "로그인 된 사용자의 정보를 확인합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자 정보 확인 성공"),
            @ApiResponse(responseCode = "400", description = "사용자 정보 확인 실패", content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDTO> getUserProfile(@AuthenticationPrincipal CustomUserDetails userDetails) {
        UserProfileResponseDTO dto = userService.getUserProfile(userDetails.getId());
        return ResponseEntity.ok().body(dto);
    }

    // 비밀번호 변경
    @Operation(summary = "사용자 비밀번호 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @ApiResponse(responseCode = "400", description = "비밀번호 변경 실패")
    })
    @PatchMapping("me/password")
    public ResponseEntity<?> updatePassword(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdatePasswordRequestDTO updateDto
            ) {
        try {
            userService.updateUserPassword(userDetails.getId(), updateDto);
            return ResponseEntity.ok(Map.of("message", "비밀번호가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // 닉네임 변경
    @Operation(summary = "사용자 닉네임 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @ApiResponse(responseCode = "400", description = "닉네임 변경 실패")
    })
    @PatchMapping("/me/nickname")
    public ResponseEntity<?> updateNickname(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody UserUpdateNicknameRequestDTO updateDto
            ) {
        try{
            userService.updateUserNickName(userDetails.getId(), updateDto);
            return ResponseEntity.ok(Map.of("message", "닉네임이 성공적으로 변경되었습니다."));
        } catch(NicknameAlreadyExistsException | SQLException e) {
            return ResponseEntity.badRequest().body(Map.of("message", "이미 존재하는 닉네임입니다."));
        }
    }

    // 프로필 사진 변경
    @Operation(summary = "사용자 프로필 이미지 변경")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "프로필 이미지 변경 성공"),
            @ApiResponse(responseCode = "400", description = "프로필 이미지 변경 실패")
    })
    @PatchMapping("/me/avatar")
    public ResponseEntity<?> updateAvatar(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestPart("file") MultipartFile file
            ) {
        try{
            userService.updateUserAvatar(userDetails.getId(), file);
            return ResponseEntity.ok(Map.of("message", "프로필 이미지가 성공적으로 변경되었습니다."));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

}
