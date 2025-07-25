package sarangbang.site.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.chat.dto.MessageHistoryResponseDto;
import sarangbang.site.chat.dto.UserChatRoomSummaryDto;
import sarangbang.site.chat.service.ChatRoomService;
import sarangbang.site.chat.service.ChatService;
import sarangbang.site.security.details.CustomUserDetails;

import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;
    private final ChatService chatService;

//    /* 채팅방 생성 */
//    @PostMapping
//    public ResponseEntity<ChatRoomSummaryResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto request, @AuthenticationPrincipal CustomUserDetails userDetails) {
//        ChatRoomSummaryResponseDto responseDto = chatRoomService.createRoom(request, userDetails.getId());
//        ResponseEntity<ChatRoomSummaryResponseDto> response = ResponseEntity.ok(responseDto);
//        return response;
//    }

    @GetMapping
    public ResponseEntity<List<UserChatRoomSummaryDto>> getAllRooms(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<UserChatRoomSummaryDto> responseDto = chatRoomService.getAllRooms(userDetails.getId());
        ResponseEntity<List<UserChatRoomSummaryDto>> response = ResponseEntity.ok(responseDto);
        return response;
    }

    /* 채팅 메시지 페이징 조회 */
    @GetMapping("/{roomId}/messages")
    public ResponseEntity<MessageHistoryResponseDto> getMessageHistory(@PathVariable String roomId, @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable, @AuthenticationPrincipal CustomUserDetails userDetails) {
        MessageHistoryResponseDto history = chatService.getMessageHistory(roomId, pageable, userDetails.getId());
        return ResponseEntity.ok(history);
    }

    /**
     * 특정 채팅방의 메시지를 모두 읽었음을 서버에 알립니다.
     * @param roomId 채팅방 ID
     * @param userDetails 현재 로그인한 사용자 정보
     * @return 성공 시 200 OK
     */
    @PostMapping("/{roomId}/read")
    public ResponseEntity<Void> markAsRead(
            @PathVariable String roomId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        chatService.markAsRead(roomId, userDetails);
        ResponseEntity<Void> response = ResponseEntity.noContent().build();

        return response;
    }
}
