package sarangbang.site.notification.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sarangbang.site.notification.component.EmitterManager;
import sarangbang.site.notification.dto.NotificationResponseDTO;
import sarangbang.site.notification.service.NotificationService;
import sarangbang.site.security.details.CustomUserDetails;
import sarangbang.site.security.jwt.JwtTokenProvider;

import java.util.List;
import java.util.UUID;

@Tag(name = "Notification", description = "알림 기능 관련 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final EmitterManager emitterManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final NotificationService notificationService;

    // SSE 구독 (ResponseEntity 예외적으로 사용 X)
    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@RequestParam("token") String token) {
        String userId = jwtTokenProvider.getUserIdFromAccessToken(token); // 사용자 ID 추출
        return emitterManager.save(userId);
    }

    // 알림 리스트 조회
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(@RequestParam("token") String token) {
        String userId = jwtTokenProvider.getUserIdFromAccessToken(token);
        List<NotificationResponseDTO> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 개별 알림 읽음 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<String> markAsRead(@PathVariable UUID notificationId) {
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok("읽음 처리 완료");
    }

    // 알림 삭제 처리
    @DeleteMapping
    public ResponseEntity<String> deleteNotifications(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String userId = userDetails.getId();
        notificationService.deleteNotifications(userId);
        return ResponseEntity.ok("알림 삭제 완료");
    }

}
