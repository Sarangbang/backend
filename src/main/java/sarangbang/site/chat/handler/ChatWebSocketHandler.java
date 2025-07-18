package sarangbang.site.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sarangbang.site.chat.dto.ChatMessage;
import sarangbang.site.chat.enums.MessageType;
import sarangbang.site.chat.service.ChatService;

/**
 * WebSocket 통신의 실제 로직을 처리하는 핸들러입니다.
 * TextWebSocketHandler를 상속받아 텍스트 기반 메시지를 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;

    /**
     * 클라이언트와 WebSocket 연결이 성공적으로 수립되었을 때 호출됩니다.
     * @param session 연결된 클라이언트의 WebSocket 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Authentication authentication = (Authentication) session.getAttributes().get("user");
        String username = authentication.getName();

        String roomId = session.getUri().getQuery().split("=")[1];

        session.getAttributes().put("roomId", roomId);
        session.getAttributes().put("username", username);

        chatService.addSessionToRoom(roomId, session);

        // 5. 다른 사용자들에게 새로운 사용자의 입장을 알리는 메시지를 보냅니다.
        // 수정된 부분: ChatMessage 생성자 순서 및 인자 수정
        ChatMessage entryMessage = new ChatMessage(MessageType.ENTER, roomId, username, username + "님이 입장하셨습니다.");
        chatService.sendMessageToRoom(roomId, entryMessage);
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출됩니다.
     * @param session 메시지를 보낸 클라이언트의 세션
     * @param message 수신된 텍스트 메시지
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);
        String roomId = (String) session.getAttributes().get("roomId");
        chatService.sendMessageToRoom(roomId, chatMessage);
    }

    /**
     * 클라이언트와의 WebSocket 연결이 종료되었을 때 호출됩니다.
     * @param session 연결이 종료된 세션
     * @param status 종료 상태 정보 (정상 종료, 에러 등)
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String roomId = (String) session.getAttributes().get("roomId");
        String username = (String) session.getAttributes().get("username");

        chatService.removeSessionFromRoom(roomId, session);

        // 다른 사용자들에게 퇴장 사실을 알리는 메시지를 보냅니다.
        // 수정된 부분: ChatMessage 생성자 순서 및 인자 수정
        ChatMessage exitMessage = new ChatMessage(MessageType.LEAVE, roomId, username, username + "님이 퇴장하셨습니다.");
        chatService.sendMessageToRoom(roomId, exitMessage);
    }
}