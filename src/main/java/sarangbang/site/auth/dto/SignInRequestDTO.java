package sarangbang.site.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "로그인 DTO")
public class SignInRequestDTO {
    @Schema(description = "이메일", example = "testuser@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "12345678")
    private String password;
}
