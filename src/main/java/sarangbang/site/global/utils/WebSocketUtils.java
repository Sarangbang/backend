package sarangbang.site.global.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import sarangbang.site.security.details.CustomUserDetails;

@Component
@Slf4j
@RequiredArgsConstructor
public class WebSocketUtils {

    private final static String USER_ATTRIBUTE = "user";

    public CustomUserDetails getCustomUserDetails(WebSocketSession session) {
        Authentication authentication = (Authentication) session.getAttributes().get(USER_ATTRIBUTE);

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        if (customUserDetails == null) {
            log.warn("세션에 사용자 정보가 없습니다. 세션 ID: {}", session.getId());
            return null;
        }
        return customUserDetails;
    }
}
