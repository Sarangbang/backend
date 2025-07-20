package sarangbang.site.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequestDto {
    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 2, max = 15, message = "닉네임은 2자 이상 15자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "성별은 필수입니다.")
    private String gender;

    @NotNull(message = "지역은 필수입니다.")
    private Long regionId;
} 