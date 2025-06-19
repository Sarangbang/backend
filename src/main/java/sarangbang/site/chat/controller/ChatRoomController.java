package sarangbang.site.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.chat.dto.ChatRoomCreateRequestDto;
import sarangbang.site.chat.dto.ChatRoomSummaryResponseDto;
import sarangbang.site.chat.dto.UserChatRoomSummaryDto;
import sarangbang.site.chat.service.ChatRoomService;
import sarangbang.site.security.details.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    /* 임시 로그인 인증 */
    @GetMapping("/api/test/test2")
    public void test(@AuthenticationPrincipal CustomUserDetails userDetails) {
        System.out.println(userDetails.getId());
    }

    @PostMapping
    public ResponseEntity<ChatRoomSummaryResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ChatRoomSummaryResponseDto responseDto = chatRoomService.createRoom(request, userDetails.getId());
        ResponseEntity<ChatRoomSummaryResponseDto> response = ResponseEntity.ok(responseDto);
        return response;
    }

    @GetMapping
    public ResponseEntity<List<UserChatRoomSummaryDto>> getAllRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserChatRoomSummaryDto> responseDto = chatRoomService.getAllRooms(userDetails.getId());
        ResponseEntity<List<UserChatRoomSummaryDto>> response = ResponseEntity.ok(responseDto);
        return response;
    }
}
