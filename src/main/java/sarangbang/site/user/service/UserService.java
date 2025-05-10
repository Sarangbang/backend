package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.exception.UserExceptionMessage;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;

    public User getUserById(String userId) throws UserNotFoundException {
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                    return new UserNotFoundException(UserExceptionMessage.USER_NOT_FOUND.getMessage().formatted(userId));
                });
    }
}
