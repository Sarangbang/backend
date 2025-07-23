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
import sarangbang.site.chat.entity.ChatMessage;
import sarangbang.site.chat.entity.ChatReadStatus;
import sarangbang.site.chat.repository.ChatMessageRepository;
import sarangbang.site.chat.repository.ChatReadStatusRepository;
import sarangbang.site.security.details.CustomUserDetails;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
        sendPreviousMessages(roomId, session);
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
            ChatMessage chatMessage = new ChatMessage(message.getRoomId(), message.getType(), message.getSender(), message.getMessage());
            chatMessageRepository.save(chatMessage);

            String messagePayload = objectMapper.writeValueAsString(chatMessage);
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

    public MessageHistoryResponseDto getMessageHistory(String roomId, Pageable pageable) {

        Slice<ChatMessage> messageSlice = chatMessageRepository.findByRoomId(roomId, pageable);
        List<ChatMessage> messages = messageSlice.getContent().stream()
                .map(doc -> new ChatMessage(doc.get_id(), doc.getRoomId(), doc.getType(), doc.getSender(), doc.getMessage(), doc.getCreatedAt())).toList();

        MessageHistoryResponseDto responseDto = new MessageHistoryResponseDto(messages, messageSlice.hasNext());
        return responseDto;
    }
}