package sarangbang.site.user.entity;

import jakarta.persistence.Entity;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.*;
import sarangbang.site.global.entity.BaseEntity;
import sarangbang.site.region.entity.Region;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity {

    @Id
    @Column(length = 36) // UUID 문자열 길이에 맞춰 컬럼 크기 지정
    private String id; // Long -> String

    private String email;

    @Column(nullable = true)
    private String password;

    @Column(unique = true)
    private String nickname;

    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    private String profileImageUrl;

    // OAuth2
    private String provider; // google
    private String providerId; // google sub
    private boolean profileComplete;

    public void updateProfile(String nickname, String gender, Region region) {
        this.nickname = nickname;
        this.gender = gender;
        this.region = region;
        this.profileComplete = true;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }
}