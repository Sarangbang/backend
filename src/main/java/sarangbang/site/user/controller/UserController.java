package sarangbang.site.user.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.user.dto.UserUpdateRequestDto;
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
}
