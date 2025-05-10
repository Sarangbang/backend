package sarangbang.site.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.user.entity.User;

import java.util.List;

public interface UserRepository extends JpaRepository<User, String> {
    List<User> findAllByJob(String job);
}
