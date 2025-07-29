package sarangbang.site.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class RefreshToken{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 512)
    private String refreshTokenValue;

    @Column(nullable = false)
    private String deviceInfo; // UUID

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime expiresAt; // 토큰 만료 시간

    @CreatedDate
    @Column(updatable = false, nullable = false)
    private LocalDateTime createdAt;

    public RefreshToken(User user, String refreshTokenValue, String deviceInfo, String ipAddress, LocalDateTime expiresAt) {
        this.user = user;
        this.refreshTokenValue = refreshTokenValue;
        this.deviceInfo = deviceInfo;
        this.ipAddress = ipAddress;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(User user, String refreshTokenValue, String ipAddress, LocalDateTime expiresAt) { // deviceInfo 파라미터 삭제

        // deviceInfo를 내부에서 직접 생성
        String deviceInfo = UUID.randomUUID().toString();

        return new RefreshToken(user, refreshTokenValue, deviceInfo, ipAddress, expiresAt);
    }
}
