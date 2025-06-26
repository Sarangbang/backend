package sarangbang.site.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequestDTO {

    @Email @NotBlank (message = "이메일은 필수 입력 항목입니다.")
    private String email;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입력해주세요.")
    private String password;

    @Size(min = 8, message = "비밀번호 확인은 최소 8자 이상 입력해주세요.")
    private String passwordConfirm;

    @NotBlank @Size(max = 15, message = "닉네임은 최대 15자까지 입니다.")
    private String nickname;

    @Pattern(regexp = "^(MALE|FEMALE)$", message = "gender 는 MALE/FEMALE 중 하나로 입력해주세요.")
    private String gender;

    @NotBlank (message = "지역은 필수 입력 항목입니다.")
    private String region;

    public boolean passwordMatched() {
        return password != null && password.equals(passwordConfirm);
    }
}
