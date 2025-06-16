package sarangbang.site.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import sarangbang.site.user.entity.User;

@Data
@NoArgsConstructor
public class UserRegisterRequestDto {
    private String email;
    private String password;
    private String gender;
    private String region;
    private String profileImageUrl;

    public User toEntity() {
        return new User(null, this.email, this.password, this.gender, this.region, this.profileImageUrl);
    }
} 