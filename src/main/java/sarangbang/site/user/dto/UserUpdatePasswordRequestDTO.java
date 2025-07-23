package sarangbang.site.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserUpdatePasswordRequestDTO {

    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    @Schema(description = "현재 비밀번호", example = "12345678")
    private String currentPassword;

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상 입력해주세요.")
    @Schema(description ="새 비밀번호", example = "23456789")
    private String newPassword;

    @NotBlank(message = "새 비밀번호 확인을 입력해주세요.")
    @Size(min = 8, message = "비밀번호 확인은 최소 8자 이상 입력해주세요.")
    @Schema(description = "새 비밀번호 확인", example = "12345678")
    private String newPasswordCheck;

    public boolean passwordMatched() {
        return newPassword.equals(newPasswordCheck);
    }
}
