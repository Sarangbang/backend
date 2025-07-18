package sarangbang.site.chat.service;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 채팅방 관리 및 메시지 발송 로직을 담당하는 서비스 클래스입니다.
 */
@Service
public class ChatService {
    // 채팅방 ID와 해당 방에 참여한 세션들의 Set을 매핑하여 관리합니다.
    // 동시성 문제를 방지하기 위해 ConcurrentHashMap을 사용합니다.
    private final Map<String, Set<WebSocketSession>> chatRooms = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public ChatService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    /**
     * 특정 채팅방에 새로운 세션을 추가합니다.
     * @param roomId 채팅방 ID
     * @param session 추가할 WebSocket 세션
     */
    public void addSessionToRoom(String roomId, WebSocketSession session) {
        // computeIfAbsent: 키(roomId)에 해당하는 값이 없으면 새로운 HashSet을 생성하고, 있으면 기존 Set을 반환합니다.
        chatRooms.computeIfAbsent(roomId, k -> ConcurrentHashMap.newKeySet()).add(session);
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

    /**
     * 특정 채팅방의 모든 세션에게 메시지를 발송(브로드캐스트)합니다.
     * @param roomId 메시지를 보낼 채팅방 ID
     * @param message 전송할 메시지 객체
     */
    public void sendMessageToRoom(String roomId, Object message) {
        if (chatRooms.containsKey(roomId)) {
            // 메시지 객체를 JSON 문자열로 변환합니다.
            try {
                String messagePayload = objectMapper.writeValueAsString(message);
                TextMessage textMessage = new TextMessage(messagePayload);

                // 해당 채팅방의 모든 세션에 대해 반복하며 메시지를 전송합니다.
                for (WebSocketSession session : chatRooms.get(roomId)) {
                    if (session.isOpen()) {
                        session.sendMessage(textMessage);
                    }
                }
            } catch (IOException e) {
                // 로깅 또는 예외 처리
                e.printStackTrace();
            }
        }
    }
}