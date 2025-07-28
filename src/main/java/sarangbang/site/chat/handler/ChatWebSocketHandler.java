package sarangbang.site.chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sarangbang.site.chat.dto.ChatMessageDto;
import sarangbang.site.chat.dto.Sender;
import sarangbang.site.chat.service.ChatService;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

/**
 * WebSocket 통신의 실제 로직을 처리하는 핸들러입니다.
 * TextWebSocketHandler를 상속받아 텍스트 기반 메시지를 처리합니다.
 */
@Component
@RequiredArgsConstructor
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    /**
     * 클라이언트와 WebSocket 연결이 성공적으로 수립되었을 때 호출됩니다.
     * @param session 연결된 클라이언트의 WebSocket 세션
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        Authentication authentication = (Authentication) session.getAttributes().get("user");
        String userEmail = authentication.getName();

        User user = userRepository.findByEmail(userEmail).orElse(null);
        Sender sender = new Sender(String.valueOf(user.getId()), user.getNickname(), user.getProfileImageUrl());
//        String roomId = ((String) session.getAttributes().get("roomId")).split("&")[0];

        String roomId = (session.getUri().getQuery().split("=")[1]).split("&")[0];

        session.getAttributes().put("roomId", roomId);
        session.getAttributes().put("sender", sender);

        chatService.addSessionToRoom(roomId, session);
    }

    /**
     * 클라이언트로부터 텍스트 메시지를 수신했을 때 호출됩니다.
     * @param session 메시지를 보낸 클라이언트의 세션
     * @param message 수신된 텍스트 메시지
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        ChatMessageDto chatMessage = objectMapper.readValue(payload, ChatMessageDto.class);
        String roomId = ((String) session.getAttributes().get("roomId")).split("&")[0];
        chatService.sendMessageToRoom(roomId, chatMessage);
    }

    /**
     * 클라이언트와의 WebSocket 연결이 종료되었을 때 호출됩니다.
     * @param session 연결이 종료된 세션
     * @param status 종료 상태 정보 (정상 종료, 에러 등)
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status){
        String roomId = ((String) session.getAttributes().get("roomId")).split("&")[0];
        Sender sender = (Sender) session.getAttributes().get("sender");

        chatService.removeSessionFromRoom(roomId, session);
    }
}