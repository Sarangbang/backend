package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.file.service.FileStorageService;
import sarangbang.site.file.service.ImageUploadService;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.service.RegionService;
import sarangbang.site.user.dto.UserProfileResponseDTO;
import sarangbang.site.user.dto.UserUpdateNicknameRequestDTO;
import sarangbang.site.user.dto.UserUpdatePasswordRequestDTO;
import sarangbang.site.user.dto.UserUpdateRequestDto;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.exception.UserExceptionMessage;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.repository.UserRepository;

import java.sql.SQLException;
import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RegionService regionService;
    private final PasswordEncoder passwordEncoder;
    private final ImageUploadService imageUploadService;
    private final FileStorageService fileStorageService;

    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                });
    }

    public void validateUserNickname(String nickname) {
        if (nickname == null || userRepository.existsByNickname(nickname)) {
            log.warn("닉네임 중복: {}", nickname);
            throw new NicknameAlreadyExistsException("이미 사용 중인 닉네임입니다.");
        }
    }

    @Transactional
    public void updateUserProfile(String userId, UserUpdateRequestDto updateDto) throws IllegalArgumentException, NicknameAlreadyExistsException {
        User user = getUserById(userId);

        validateUserNickname(updateDto.getNickname());

        Region region = regionService.findRegionById(updateDto.getRegionId());

        user.updateProfile(updateDto.getNickname(), updateDto.getGender(), region);
    }

    // 로그인 된 사용자 정보 확인
    public UserProfileResponseDTO getUserProfile(String userId) {
        User user = getUserById(userId);

        UserProfileResponseDTO dto = new UserProfileResponseDTO(
                user.getEmail(),
                user.getNickname(),
                fileStorageService.generatePresignedUrl(user.getProfileImageUrl(), Duration.ofMinutes(10)),
                user.getGender(),
                user.getRegion().getFullAddress()
        );

        return dto;
    }

    // 비밀번호 변경
    @Transactional
    public void updateUserPassword(String userId, UserUpdatePasswordRequestDTO updateDto) {
        User user = getUserById(userId);

        if(!passwordEncoder.matches(updateDto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        if(!updateDto.passwordMatched()) {
            throw new IllegalArgumentException("새 비밀번호가 일치하지 않습니다.");
        }

        String encodeNewPassword = passwordEncoder.encode(updateDto.getNewPassword());
        user.updatePassword(encodeNewPassword);
    }

    // 닉네임 변경
    @Transactional
    public void updateUserNickName(String userId, UserUpdateNicknameRequestDTO updateDto) throws SQLException {
        User user = getUserById(userId);

        validateUserNickname(updateDto.getNickname());

        user.updateNickname(updateDto.getNickname());
    }

    // 프로필 이미지 변경
    @Transactional
    public void updateUserAvatar(String userId, MultipartFile file) {
        User user = getUserById(userId);

        String key = imageUploadService.storeProfileImage(file, userId);

        user.updateProfileImageUrl(key);
    }
}
