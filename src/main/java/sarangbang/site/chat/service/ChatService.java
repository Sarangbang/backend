package sarangbang.site.chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import sarangbang.site.chat.dto.ChatMessageDto;
import sarangbang.site.chat.dto.MessageHistoryResponseDto;
import sarangbang.site.chat.dto.Sender;
import sarangbang.site.chat.entity.ChatMessage;
import sarangbang.site.chat.entity.ChatReadStatus;
import sarangbang.site.chat.entity.ChatRoom;
import sarangbang.site.chat.repository.ChatMessageRepository;
import sarangbang.site.chat.repository.ChatReadStatusRepository;
import sarangbang.site.chat.repository.ChatRoomRepository;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.user.dto.UserProfileResponseDTO;
import sarangbang.site.user.service.UserService;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅방 관리 및 메시지 발송 로직을 담당하는 서비스 클래스입니다.
 */
@Service
@RequiredArgsConstructor
public class ChatService {
    // 채팅방 ID와 해당 방에 참여한 세션들의 Set을 매핑하여 관리합니다.
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap을 사용합니다.
    private final Map<String, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;
    private final ChatMessageRepository chatMessageRepository;
    private final ChatReadStatusRepository chatReadStatusRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserService userService;

    /**
     * 특정 채팅방의 메시지를 사용자가 모두 읽었음을 기록합니다.
     * @param roomId 채팅방 ID
     * @param userDetails 현재 로그인한 사용자 정보
     */
    @Transactional
    public void markAsRead(String roomId, CustomUserDetails userDetails) {
        String userId = userDetails.getId(); // 사용자 ID 추출

        // 1. roomId와 userId로 기존 '읽음 상태' 정보가 있는지 조회합니다.
        ChatReadStatus readStatus = chatReadStatusRepository.findByUserIdAndRoomId(userId, roomId)
                .orElse(new ChatReadStatus(userId, roomId)); // 없으면 새로 생성합니다.

        // 2. 마지막으로 읽은 시간을 현재 시간으로 갱신합니다.
        readStatus.updateLastReadAt();

        // 3. 변경된 상태를 DB에 저장합니다. (새로 생성했거나, 기존 정보를 업데이트)
        chatReadStatusRepository.save(readStatus);
    }

    /**
     * 특정 채팅방에 새로운 세션을 추가합니다.
     * @param roomId 채팅방 ID
     * @param session 추가할 WebSocket 세션
     */
    public void addSessionToRoom(String roomId, WebSocketSession session) {
        // computeIfAbsent: 키(roomId)에 해당하는 값이 없으면 새로운 HashSet을 생성하고, 있으면 기존 Set을 반환합니다.
        chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
        // 이전 대화 기록을 조회하여 새로운 세션에만 전송
//        sendPreviousMessages(roomId, session);
    }

    private void sendPreviousMessages(String roomId, WebSocketSession session) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomIdOrderByCreatedAtAsc(roomId);
        try {
            for (ChatMessage message : messages) {
                String messagePayload = objectMapper.writeValueAsString(message);
                session.sendMessage(new TextMessage(messagePayload));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 특정 채팅방에서 세션을 제거합니다. (사용자 퇴장 시)
     * @param roomId 채팅방 ID
     * @param session 제거할 WebSocket 세션
     */
    public void removeSessionFromRoom(String roomId, WebSocketSession session) {
        if (chatRooms.containsKey(roomId)) {
            chatRooms.get(roomId).remove(session);
            // 만약 방에 아무도 남지 않으면, 맵에서 해당 채팅방을 제거하여 메모리를 관리합니다.
            if (chatRooms.get(roomId).isEmpty()) {
                chatRooms.remove(roomId);
            }
        }
    }

   
    public void sendMessageToRoom(String roomId, ChatMessageDto message) {
        // 메시지 객체를 JSON 문자열로 변환합니다.
        try {
            ChatMessage chatMessage = new ChatMessage(message.getRoomId(), message.getType(), message.getSender().getUserId(), message.getMessage());
            chatMessageRepository.save(chatMessage);

            UserProfileResponseDTO user = userService.getUserProfile(message.getSender().getUserId());
            ChatMessageDto chatMessageDto = new ChatMessageDto(
                    chatMessage.get_id(),
                    chatMessage.getType(),
                    chatMessage.getRoomId(),
                    new Sender(user.getId(), user.getNickname(), user.getProfileImageUrl()),
                    chatMessage.getMessage(),
                    chatMessage.getCreatedAt()
            );
            String messagePayload = objectMapper.writeValueAsString(chatMessageDto);
            TextMessage textMessage = new TextMessage(messagePayload);

            // 해당 채팅방의 모든 세션에 대해 반복하며 메시지를 전송합니다.
            if (chatRooms.containsKey(roomId)) {
                for (WebSocketSession session : chatRooms.get(roomId)) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
            }
        } catch (IOException e) {
            // 로깅 또는 예외 처리
            e.printStackTrace();
        }
    }

    /**
     * 특정 채팅방의 메시지 내역을 조회하며, 내가 보낸 메시지의 '안 읽은 수'를 계산합니다.
     */
    @Transactional
    public MessageHistoryResponseDto getMessageHistory(String roomId, Pageable pageable, String userId) {
        // 1. 채팅방의 전체 참여자 목록을 미리 조회합니다.
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId)
                .orElseThrow(() -> new IllegalArgumentException("채팅방을 찾을 수 없습니다: " + roomId));
        List<String> allParticipantIds = chatRoom.getParticipants();

        // 2. 해당 채팅방의 모든 '읽음 상태' 정보를 Map으로 변환하여 성능을 최적화합니다.
        Map<String, LocalDateTime> readStatusMap = new HashMap<>();

        List<ChatReadStatus> readStatusList = chatReadStatusRepository.findByRoomId(roomId);

        for (ChatReadStatus status : readStatusList) {
            readStatusMap.put(status.getUserId(), status.getLastReadAt());
        }

        // 3. DB에서 메시지 목록을 페이지네이션하여 조회합니다.
        Slice<ChatMessage> messageSlice = chatMessageRepository.findByRoomId(roomId, pageable);

        List<ChatMessageDto> messageDtoList = new ArrayList<>();
        List<ChatMessage> messages = messageSlice.getContent();

        for (ChatMessage message : messages) {
            UserProfileResponseDTO user = userService.getUserProfile(message.getSender());
            ChatMessageDto dto = new ChatMessageDto(
                    message.get_id(),
                    message.getType(),
                    message.getRoomId(),
                    new Sender(user.getId(), user.getNickname(), user.getProfileImageUrl()),
                    message.getMessage(),
                    message.getCreatedAt()
            );

            // 현재 사용자가 보낸 메시지에 대해서만 안 읽은 수를 계산
            if (message.getSender().equals(userId)) {
                int unreadCount = calculateUnreadCount(message, allParticipantIds, readStatusMap);
                dto.setUnreadCount(unreadCount);
            }

            messageDtoList.add(dto);
        }

        MessageHistoryResponseDto responseDto = new MessageHistoryResponseDto(messageDtoList, messageSlice.hasNext());

        return responseDto;
    }

    /**
     * 특정 메시지에 대해 안 읽은 사람 수를 계산하는 핵심 메서드입니다.
     * @param message 기준 메시지
     * @param allParticipantIds 채팅방 전체 참여자 ID 목록
     * @param readStatusMap 채팅방 참여자들의 마지막 읽은 시간 Map
     * @return 안 읽은 사람 수
     */
    private int calculateUnreadCount(ChatMessage message, List<String> allParticipantIds, Map<String, LocalDateTime> readStatusMap) {
        String senderId = message.getSender();

        // 1. 전체 참여자 중에서
        int unreadCount = 0;
        for (String participantId : allParticipantIds) {
            // 2. 메시지를 보낸 자신을 제외하고
            if (participantId.equals(senderId)) {
                continue;
            }

            // 3. 각 참여자의 마지막 읽은 시간(lastReadAt)을 가져와서
            // 4. 메시지가 생성된 시간(createdAt)과 비교합니다.
            // 읽은 기록이 없으면 아주 오래된 시간(EPOCH)으로 간주
            LocalDateTime lastReadAt = readStatusMap.getOrDefault(participantId, LocalDateTime.MIN);

            // 마지막 읽은 시간이 메시지 생성 시간보다 이전이면 '안 읽음' 상태
            // 5. 조건을 만족하는 사용자의 총 수를 계산합니다.
            if (lastReadAt.isBefore(message.getCreatedAt())) {
                unreadCount++;
            }
        }

        return unreadCount;
    }
}