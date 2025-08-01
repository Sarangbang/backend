package sarangbang.site.notification.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sarangbang.site.notification.entity.Notification;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByReceiverIdOrderByCreatedAtDesc(String userId);
    void deleteAllByReceiverId(String userId);
}
