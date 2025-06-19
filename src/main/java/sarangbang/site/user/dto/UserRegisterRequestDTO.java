package sarangbang.site.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sarangbang.site.user.entity.User;

import java.util.UUID;

@Data
@NoArgsConstructor
public class UserRegisterRequestDTO {
    private String email;
    private String password;
    private String gender;
    private String region;
    private String profileImageUrl;
    private String nickname;

    public User toEntity() {
        return new User(UUID.randomUUID().toString(), this.email, this.password, this.gender, this.region, this.profileImageUrl, this.nickname);
    }
} 