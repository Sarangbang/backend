package sarangbang.site.notification.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.notification.entity.Notification;

import java.time.LocalDateTime;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class NotificationResponseDTO {

    @Schema(description = "알림 Id", example = "TEST_Notification")
    private UUID id;
    @Schema(description = "수신 타입", example = "challenge_apply")
    private String type;
    @Schema(description = "내용", example = "새로운 챌린지 신청서가 도착했습니다.")
    private String content;
    @Schema(description = "알림 클릭 시 이동할 url", example = "/challenge/application/{applicationId}")
    private String url;
    @Schema(description = "알림 확인 여부", example = "true")
    private boolean isRead;
    @Schema(description = "알림 생성일", example = "2025-07-29")
    private LocalDateTime createdAt;


    public static NotificationResponseDTO from(Notification notification) {
        return NotificationResponseDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .content(notification.getContent())
                .url(notification.getUrl())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
