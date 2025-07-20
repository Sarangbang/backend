package sarangbang.site.security.oauth.userinfo;

import java.util.Map;

public class KakaoUserInfo implements OAuth2UserInfo {

    private final Map<String, Object> attributes;
    private final Map<String, Object> kakaoAccountAttributes;
    private final Map<String, Object> profileAttributes;

    public KakaoUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
        this.kakaoAccountAttributes = (Map<String, Object>) attributes.get("kakao_account");
        this.profileAttributes = (Map<String, Object>) kakaoAccountAttributes.get("profile");
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getProvider() {
        return "kakao";
    }

    @Override
    public String getEmail() {
        return (String) kakaoAccountAttributes.get("email");
    }

    @Override
    public String getName() {
        return null;
    }
    
    @Override
    public String getProfileImageUrl() {
        return null;
    }
} 