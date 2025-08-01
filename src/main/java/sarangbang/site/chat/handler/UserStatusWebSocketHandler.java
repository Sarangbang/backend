package sarangbang.site.chat.handler;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import sarangbang.site.chat.service.ChatService;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class UserStatusWebSocketHandler extends TextWebSocketHandler {

    private final ChatService chatService;
    private final UserRepository userRepository;

    // 연결된 세션을 ChatService의 userSessions 맵에 등록
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        Authentication authentication = (Authentication) session.getAttributes().get("user");
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user email"));
        String userId = user.getId();

        session.getAttributes().put("userId", userId);
        chatService.addUserSession(userId, session);
    }

//    @Override
//    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
//        // 대기방 핸들러는 특별한 메시지 처리가 필요 없음. (하트비트 등 추후 확장 가능)
//    }

    // userSessions 맵에서 세션 제거
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            chatService.removeUserSession(userId, session);
        }
    }
}
