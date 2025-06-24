package sarangbang.site.auth.dto;

import lombok.Data;

@Data
public class SignInRequest {
    private String email;
    private String password;
}
