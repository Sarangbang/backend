package sarangbang.site.chat.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.chat.dto.ChatRoomCreateRequestDto;
import sarangbang.site.chat.dto.ChatRoomSummaryResponseDto;
import sarangbang.site.chat.dto.UserChatRoomSummaryDto;
import sarangbang.site.chat.entity.ChatReadStatus;
import sarangbang.site.chat.entity.ChatRoom;
import sarangbang.site.chat.repository.ChatMessageRepository;
import sarangbang.site.chat.repository.ChatReadStatusRepository;
import sarangbang.site.chat.repository.ChatRoomRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;

    /* 채팅방 생성 */
    public ChatRoomSummaryResponseDto createRoom(ChatRoomCreateRequestDto request, String userId) {
        ChatRoom chatRoom = new ChatRoom(
                UUID.randomUUID().toString(),
                request.getRoomType(),
                request.getSourceType(),
                request.getSourceId(),
                request.getRoomName(),
                userId,
                request.getParticipants(),
                LocalDateTime.now(),
                request.getChallengeImageUrl()
        );

        ChatRoom saved = chatRoomRepository.save(chatRoom);

        ChatRoomSummaryResponseDto responseDto = new ChatRoomSummaryResponseDto(
                saved.getRoomId(),
                saved.getRoomName(),
                saved.getCreatorId(),
                saved.getParticipants(),
                saved.getCreatedAt(),
                saved.getAvatar());

        return responseDto;
    }

    /* 사용자가 참여한 모든 채팅방 조회 (안 읽은 메시지 수 포함) */
    public List<UserChatRoomSummaryDto> getAllRooms(String userId) {
        // 1. 사용자가 참여한 모든 채팅방 목록을 조회합니다.
        List<ChatRoom> roomList = chatRoomRepository.findByParticipantsContaining(userId);
        List<UserChatRoomSummaryDto> roomSummaries = new ArrayList<>();

        // 2. 사용자의 모든 '읽음 상태' 정보를 조회하여 Map으로 변환합니다. (Key: roomId, Value: lastReadAt)
        //    이렇게 하면 각 채팅방의 마지막 읽은 시간을 빠르게 찾을 수 있습니다.
        Map<String, LocalDateTime> readStatusMap = chatReadStatusRepository.findByUserId(userId)
                .stream()
                .collect(Collectors.toMap(ChatReadStatus::getRoomId, ChatReadStatus::getLastReadAt));

        // 3. 채팅방 목록을 순회하며 안 읽은 메시지 수를 계산하고 DTO로 변환합니다.
        roomSummaries = roomList.stream().map(room -> {
            // 해당 채팅방의 마지막 읽은 시간을 Map에서 조회합니다.
            // 만약 한 번도 읽지 않았다면(null), 아주 오래된 시간(LocalDateTime.EPOCH)을 기본값으로 사용합니다.
            LocalDateTime lastReadAt = readStatusMap.getOrDefault(room.getRoomId(), LocalDateTime.now().minusYears(1));

            // 마지막으로 읽은 시간 이후에 온 메시지의 개수를 DB에서 조회합니다.
            long unreadCount = chatMessageRepository.countByRoomIdAndSenderNotAndCreatedAtAfter(room.getRoomId(), userId, lastReadAt);

            // 최종 DTO를 생성합니다.
            UserChatRoomSummaryDto summaryDto = new UserChatRoomSummaryDto(
                    room.getRoomId(),
                    room.getRoomName(),
                    room.getCreatorId(),
                    room.getParticipants(),
                    room.getCreatedAt(),
                    room.getAvatar(),
                    unreadCount // 계산된 안 읽은 메시지 수를 DTO에 포함
            );
            return summaryDto;
        }).collect(Collectors.toList());

        // 생성된 DTO 리스트를 반환합니다.
        return roomSummaries;
    }
}
