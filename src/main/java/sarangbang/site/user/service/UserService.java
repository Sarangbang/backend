package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.user.dto.SignupRequestDTO;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.exception.UserExceptionMessage;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(SignupRequestDTO requestDto) {
        log.debug("▶️ 회원가입 요청  email={}, nickname={}", requestDto.getEmail(), requestDto.getNickname());

        if (!requestDto.passwordMatched()) {
            log.warn("❌ 비밀번호 불일치  email={}", requestDto.getEmail());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            log.warn("❌ 중복 이메일  email={}", requestDto.getEmail());
            throw new IllegalStateException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            log.warn("❌ 중복 닉네임  nickname={}", requestDto.getNickname());
            throw new IllegalStateException("이미 사용 중인 닉네임입니다.");
        }

        String hash = passwordEncoder.encode(requestDto.getPassword());

        String newUserId = UUID.randomUUID().toString();

        User user = new User(
                newUserId,
                requestDto.getEmail(),
                hash,
                requestDto.getNickname(),
                requestDto.getGender(),
                requestDto.getRegion(),
                null
        );

        User saved = userRepository.save(user);

        log.info("✅ 회원가입 완료  id={} email={}", saved.getId(), saved.getEmail());
        return saved.getId();
    }

    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                });
    }

}
