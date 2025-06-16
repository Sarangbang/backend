package sarangbang.site.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.user.dto.UserRegisterRequestDto;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.exception.UserAlreadyExistsException;
import sarangbang.site.user.exception.UserExceptionMessage;
import sarangbang.site.user.exception.UserNotFoundException;
import sarangbang.site.user.repository.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(UserRegisterRequestDto requestDto) {
        userRepository.findByEmail(requestDto.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException(
                            UserExceptionMessage.USER_ALREADY_EXISTS.getMessage().formatted(requestDto.getEmail()));
                });
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        User user = requestDto.toEntity();
        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

}
