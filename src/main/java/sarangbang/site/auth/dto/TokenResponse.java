package sarangbang.site.auth.dto;

public class TokenResponse {
    private String accessToken;

    public TokenResponse(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
}
