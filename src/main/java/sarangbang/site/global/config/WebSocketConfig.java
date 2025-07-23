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

    // 직접 구현할 WebSocket 핸들러와 인터셉터를 주입받습니다.
    private final ChatWebSocketHandler chatWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    /**
     * WebSocket 핸들러를 등록하고, 연결 경로와 인터셉터를 설정합니다.
     * @param registry WebSocket 핸들러를 등록할 수 있는 레지스트리 객체
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry
        // addHandler: 어떤 핸들러를 사용할지 지정합니다.
        // "/ws/chat": 클라이언트가 WebSocket 연결을 맺을 엔드포인트 경로입니다.
        .addHandler(chatWebSocketHandler, "/ws/chat")
        // addInterceptors: 핸드셰이크 과정에 개입할 인터셉터를 추가합니다.
        // 여기서는 JWT 인증을 위해 구현한 인터셉터를 등록합니다.
        .addInterceptors(jwtHandshakeInterceptor)
        // setAllowedOrigins: CORS(Cross-Origin Resource Sharing) 문제를 해결하기 위해
        // 허용할 출처를 지정합니다. "*"는 모든 출처를 허용하는 것으로,
        // 프로덕션 환경에서는 보안을 위해 특정 도메인 주소로 변경해야 합니다.
        // 예: .setAllowedOrigins("https://mychatapp.com")
        .setAllowedOrigins("*");
    }
}
