package sarangbang.site.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args){
        userRepository.findByEmail("testuser@example.com").ifPresentOrElse(
                user -> System.out.println("✅ 테스트 유저 이미 존재"),
                () -> {
                    User user = new User(
                            "testuserUUID",
                            "testuser@example.com",
                            passwordEncoder.encode("testpass"),
                            "M",
                            "Seoul",
                            null
                    );
                    userRepository.save(user);
                    System.out.println("✅ 테스트 유저 삽입 완료");
                }
        );
    }
}