package sarangbang.site.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.auth.entity.RefreshToken;
import sarangbang.site.auth.repository.RefreshTokenRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional
    public void saveToken(String userId, String refreshTokenValue) {

        Optional<RefreshToken> existingToken = refreshTokenRepository.findByUserId(userId);

        if (existingToken.isPresent()) {
            existingToken.get().updateToken(refreshTokenValue);
        } else {
            RefreshToken newToken = new RefreshToken(userId, refreshTokenValue);
            refreshTokenRepository.save(newToken);
        }
    }

    @Transactional
    public Optional<String> findTokenByUserId(String userId) {
        return refreshTokenRepository.findByUserId(userId)
                .map(RefreshToken::getRefreshToken);
    }

    @Transactional
    public void deleteToken(String userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}
