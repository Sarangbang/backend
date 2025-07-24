package sarangbang.site.auth.dto;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
@Schema(description = "회원 가입용 DTO")
public class SignupRequestDTO {

    @Email @NotBlank (message = "이메일은 필수 입력 항목입니다.")
    @Schema(description = "이메일", example = "testuser2@example.com")
    private String email;

    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입력해주세요.")
    @Schema(description = "비밀번호", example = "12345678")
    private String password;

    @Size(min = 8, message = "비밀번호 확인은 최소 8자 이상 입력해주세요.")
    @Schema(description = "비밀번호 확인", example = "12345678")
    private String passwordConfirm;

    @NotBlank @Size(max = 15, message = "닉네임은 최대 15자까지 입니다.")
    @Schema(description = "닉네임", example = "TEST-NICKNAME2")
    private String nickname;

    @Pattern(regexp = "^(MALE|FEMALE)$", message = "gender 는 MALE/FEMALE 중 하나로 입력해주세요.")
    @Schema(description = "성별", example = "MALE")
    private String gender;

    @NotNull(message = "지역은 필수 입력 항목입니다.")
    @Schema(description = "지역 ID", example = "182")
    private Long regionId;

    @Schema(description = "프로필 사진", example = "profiles/728629df-7893-462a-9c52-fee0861434e3/profile.jpg")
    private MultipartFile profileImage;

    public boolean passwordMatched() {
        return password != null && password.equals(passwordConfirm);
    }
}
