package sarangbang.site.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.auth.entity.RefreshToken;
import sarangbang.site.user.entity.User;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByRefreshTokenValue(String refreshTokenValue);

    void deleteByRefreshTokenValue(String refreshTokenValue);

    List<RefreshToken> findAllByUserOrderByCreatedAtDesc(User user); // 특정 사용자의 모든 리프레시 토큰을 찾는 메서드
}
