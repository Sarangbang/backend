package sarangbang.site.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.user.entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
}
