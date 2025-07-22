package sarangbang.site.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserProfileResponseDTO {

    private String email;
    private String nickname;
    private String profileImageUrl;
    private String gender;
    private String region;
}
