package sarangbang.site.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.auth.dto.SignupRequestDTO;
import sarangbang.site.auth.exception.EmailAlreadyExistsException;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.service.RegionService;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegionService regionService;

    @Transactional
    public String register(SignupRequestDTO requestDto) {
        log.debug("▶️ 회원가입 요청  email={}, nickname={}", requestDto.getEmail(), requestDto.getNickname());

        if (!requestDto.passwordMatched()) {
            log.warn("❌ 비밀번호 불일치  email={}", requestDto.getEmail());
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }
        if (userRepository.existsByEmail(requestDto.getEmail())) {
            log.warn("❌ 중복 이메일  email={}", requestDto.getEmail());
            throw new EmailAlreadyExistsException("이미 가입된 이메일입니다.");
        }
        if (userRepository.existsByNickname(requestDto.getNickname())) {
            log.warn("❌ 중복 닉네임  nickname={}", requestDto.getNickname());
            throw new NicknameAlreadyExistsException("이미 사용 중인 닉네임입니다.");
        }

        String hash = passwordEncoder.encode(requestDto.getPassword());
        String newUserId = UUID.randomUUID().toString();

        Region region = regionService.findRegionById(requestDto.getRegionId());


        User user = new User(
                newUserId,
                requestDto.getEmail(),
                hash,
                requestDto.getNickname(),
                requestDto.getGender(),
                region,
                null
        );

        User saved = userRepository.save(user);

        log.info("✅ 회원가입 완료  id={} email={}", saved.getId(), saved.getEmail());
        return saved.getId();
    }
}
