package sarangbang.site.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.auth.entity.RefreshToken;
import sarangbang.site.auth.repository.RefreshTokenRepository;
import sarangbang.site.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void issueNewToken(User user, String refreshTokenValue, String deviceInfo, String ipAddress, LocalDateTime expiresAt) { // 파라미터명 변경

        RefreshToken newRefreshToken = RefreshToken.create(
                user,
                refreshTokenValue, // 변경된 파라미터명 전달
                deviceInfo,
                ipAddress,
                expiresAt
        );

        refreshTokenRepository.save(newRefreshToken);
    }

    @Transactional
    public void deleteTokenByRefreshTokenValue(String refreshTokenValue) { // 메서드명 및 파라미터명 변경
        refreshTokenRepository.deleteByRefreshTokenValue(refreshTokenValue); // 변경된 리포지토리 메서드 호출
    }

    /**
     * 리프레시 토큰 값으로 토큰 객체를 조회합니다.
     * @param refreshTokenValue 조회할 리프레시 토큰 값
     * @return Optional<RefreshToken>
     */
    @Transactional(readOnly = true)
    public Optional<RefreshToken> findTokenByRefreshTokenValue(String refreshTokenValue) {
        return refreshTokenRepository.findByRefreshTokenValue(refreshTokenValue);
    }

    /**
     * 특정 사용자가 발급받은 모든 리프레시 토큰 목록을 조회합니다.
     * @param user 조회할 사용자
     * @return 해당 사용자의 리프레시 토큰 리스트
     */
    @Transactional(readOnly = true) // 단순 조회이므로 readOnly=true 옵션으로 성능 최적화
    public List<RefreshToken> findAllTokensByUser(User user) {
        return refreshTokenRepository.findAllByUserOrderByCreatedAtDesc(user);
    }
}
