package sarangbang.site.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import org.springframework.web.util.UriComponentsBuilder;
import sarangbang.site.security.jwt.JwtTokenProvider;

import java.util.Map;

/**
 * WebSocket 핸드셰이크(연결) 요청을 가로채는 인터셉터입니다.
 * 연결을 맺기 전에 클라이언트의 신원을 JWT를 통해 확인합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtHandshakeInterceptor implements HandshakeInterceptor {

    private final JwtTokenProvider jwtTokenProvider; // 직접 구현한 JWT 유틸리티 클래스

    /**
     * 핸드셰이크가 시작되기 전에 호출되는 메서드입니다.
     * @param request 들어오는 HTTP 요청
     * @param response 나가는 HTTP 응답
     * @param wsHandler 연결될 WebSocket 핸들러
     * @param attributes WebSocket 세션에 전달할 속성 맵
     * @return boolean 연결을 계속 진행할지(true), 중단할지(false) 결정
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {

        // 기존 헤더 방식 (주석 처리 또는 삭제)
        // String authHeader = request.getHeaders().getFirst("Authorization");

        // 수정된 쿼리 파라미터 방식
        String token = UriComponentsBuilder.fromUri(request.getURI())
                .build()
                .getQueryParams()
                .getFirst("token");

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            attributes.put("user", authentication);
            return true;
        }

        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        return false;
    }

    /**
     * 핸드셰이크가 완료된 후에 호출되는 메서드입니다.
     * 성공/실패 여부와 관계없이 호출되며, 리소스 정리 등의 작업을 할 수 있습니다.
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 특별히 처리할 로직이 없으면 비워둡니다.
    }
}