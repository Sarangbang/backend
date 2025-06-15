package sarangbang.site.user.entity;

import jakarta.persistence.Entity;
import lombok.Getter;

import jakarta.persistence.*;
import lombok.*;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID) // IDENTITY -> UUID
    @Column(length = 36) // UUID 문자열 길이에 맞춰 컬럼 크기 지정
    private String id; // Long -> String

    private String email;

    private String password;

    private String gender;

    private String region;

    private String profileImageUrl;

    public User(String id, String email, String password, String gender, String region, String profileImageUrl) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.gender = gender;
        this.region = region;
        this.profileImageUrl = profileImageUrl;
    }

}
