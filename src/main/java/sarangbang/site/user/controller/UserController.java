package sarangbang.site.user.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import sarangbang.site.user.dto.UserUpdateNicknameRequestDTO;
import sarangbang.site.user.dto.UserUpdatePasswordRequestDTO;
import sarangbang.site.user.dto.UserUpdateRequestDto;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.service.UserService;


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
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
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
            userService.updateUserNickName(userDetails.getNickname(), updateDto);
            return ResponseEntity.ok().build();
        } catch(NicknameAlreadyExistsException | UserNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 프로필 사진 변경
    /*@Operation(summary = "사용자 프로필 이미지 변경")
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
            return ResponseEntity.ok().build();
        } catch () {

        }
    }*/

}
