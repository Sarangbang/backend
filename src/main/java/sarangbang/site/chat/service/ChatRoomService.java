package sarangbang.site.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.chat.dto.ChatRoomCreateRequestDto;
import sarangbang.site.chat.dto.ChatRoomSummaryResponseDto;
import sarangbang.site.chat.entity.ChatRoom;
import sarangbang.site.chat.repository.ChatRoomRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;

    /* 채팅방 생성 */
    public ChatRoomSummaryResponseDto createRoom(ChatRoomCreateRequestDto request, String userId) {
        ChatRoom chatRoom = new ChatRoom(
                UUID.randomUUID().toString(),
                request.getRoomName(),
                userId,
                request.getParticipants(),
                Instant.now()
        );

        ChatRoom saved = chatRoomRepository.save(chatRoom);

        ChatRoomSummaryResponseDto responseDto = new ChatRoomSummaryResponseDto(
                saved.getRoomId(),
                saved.getRoomName(),
                saved.getCreatorId(),
                saved.getParticipants(),
                saved.getCreatedAt());

        return responseDto;
    }

    /* 채팅방 조회 */
    public List<ChatRoomSummaryResponseDto> getAllRooms(String userId) {
        List<ChatRoom> roomList = chatRoomRepository.findByParticipantsContaining(userId);
        List<ChatRoomSummaryResponseDto> roomSummaries = new ArrayList<>();

        for (ChatRoom room : roomList) {
            roomSummaries.add(new ChatRoomSummaryResponseDto(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getCreatorId(),
                    room.getParticipants(),
                    room.getCreatedAt()
            ));
        }
        return roomSummaries;
    }
}
