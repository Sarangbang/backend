package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.auth.exception.NicknameAlreadyExistsException;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.service.RegionService;
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
    private final RegionService regionService;

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
}
