package sarangbang.site.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.user.dto.UserRegisterRequestDto;
import sarangbang.site.user.service.UserService;

import java.net.URI;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Void> register(@RequestBody UserRegisterRequestDto requestDto) {
        String userId = userService.register(requestDto);
        return ResponseEntity.created(URI.create("/api/users/" + userId)).build();
    }
} 