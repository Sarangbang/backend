package sarangbang.site.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateNicknameRequestDTO {

    @NotBlank(message = "새 비밀번호를 입력해주세요.")
    @Size(max = 15, message = "닉네임은 최대 15자까지 입니다.")
    @Schema(description = "닉네임", example = "TEST-NICKNAME2")
    private String nickname;
}
