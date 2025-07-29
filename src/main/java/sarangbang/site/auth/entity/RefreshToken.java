package sarangbang.site.auth.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "refresh_tokens")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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
    private String deviceInfo;

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

    public static RefreshToken create(User user, String refreshTokenValue, String deviceInfo, String ipAddress, LocalDateTime expiresAt) {
        return new RefreshToken(user, refreshTokenValue, deviceInfo, ipAddress, expiresAt);
    }
}
