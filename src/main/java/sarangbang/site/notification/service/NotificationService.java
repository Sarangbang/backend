package sarangbang.site.notification.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.notification.component.EmitterManager;
import sarangbang.site.notification.dto.NotificationResponseDTO;
import sarangbang.site.notification.entity.Notification;
import sarangbang.site.notification.repository.NotificationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final EmitterManager emitterManager;

    /**
     * 알림 생성 및 실시간 전송
     */
    public void sendNotification(String receiverId, String content, String type, String url) {
        // 1. DB 저장
        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .content(content)
                .type(type)
                .url(url)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // 2. SSE 전송
        emitterManager.send(receiverId, NotificationResponseDTO.from(saved));
    }

    /**
     * 로그인한 유저의 전체 알림 조회
     */
    public List<NotificationResponseDTO> getUserNotifications(String userId) {
        return notificationRepository.findByReceiverIdOrderByCreatedAtDesc(userId).stream()
                .map(NotificationResponseDTO::from)
                .collect(Collectors.toList());
    }

    /**
     * 알림 읽음 처리
     */
    @Transactional
    public void markAsRead(UUID notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("알림을 찾을 수 없습니다."));
        notification.updateIsRead(true);
    }

    /**
     *  알림 삭제 처리
     */
    @Transactional
    public void deleteNotifications(String userId) {
        notificationRepository.deleteAllByReceiverId(userId);
    }
}
