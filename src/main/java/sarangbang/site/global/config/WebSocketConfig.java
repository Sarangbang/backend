package sarangbang.site.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import sarangbang.site.chat.handler.ChatWebSocketHandler;

/**
 * WebSocket 관련 주요 설정을 정의하는 클래스입니다.
 * @EnableWebSocket 어노테이션을 통해 WebSocket 기능을 활성화합니다.
 */
@Configuration
@EnableWebSocket // 순수 WebSocket을 사용하기 위한 어노테이션
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    // 직접 구현할 WebSocket 핸들러와 인터셉터를 주입
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
        .addHandler(chatWebSocketHandler, "/ws/chat")
        .addInterceptors(jwtHandshakeInterceptor)
        .setAllowedOrigins("*");
    }
}

