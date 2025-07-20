package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.repository.RegionRepository;
import sarangbang.site.user.dto.UserUpdateRequestDto;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.exception.UserExceptionMessage;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RegionRepository regionRepository;

    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                });
    }

    @Transactional
    public void updateUserProfile(String userId, UserUpdateRequestDto updateDto) {
        User user = getUserById(userId);

        Region region = regionRepository.findById(updateDto.getRegionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 지역을 찾을 수 없습니다."));

        // User 엔티티에 setter 대신 update 메소드 추가 고려
        user.updateProfile(updateDto.getNickname(), updateDto.getGender(), region);
    }
}
