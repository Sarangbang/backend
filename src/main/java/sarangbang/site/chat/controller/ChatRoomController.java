package sarangbang.site.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.chat.dto.ChatRoomCreateRequestDto;
import sarangbang.site.chat.dto.ChatRoomSummaryResponseDto;
import sarangbang.site.chat.dto.UserChatRoomSummaryDto;
import sarangbang.site.chat.service.ChatRoomService;
import java.util.List;

@RestController
@RequestMapping("/api/chat/rooms")
@RequiredArgsConstructor
public class ChatRoomController {

    private final ChatRoomService chatRoomService;

    @PostMapping
    public ResponseEntity<ChatRoomSummaryResponseDto> createRoom(@RequestBody ChatRoomCreateRequestDto request) {
        String userId = "UUID1";
        ChatRoomSummaryResponseDto responseDto = chatRoomService.createRoom(request, userId);
        ResponseEntity<ChatRoomSummaryResponseDto> response = ResponseEntity.ok(responseDto);
        return response;
    }

    @GetMapping
    public ResponseEntity<List<UserChatRoomSummaryDto>> getAllRooms() {
        String userId = "UUID1";
        List<UserChatRoomSummaryDto> responseDto = chatRoomService.getAllRooms(userId);
        ResponseEntity<List<UserChatRoomSummaryDto>> response = ResponseEntity.ok(responseDto);
        return response;
    }
}
