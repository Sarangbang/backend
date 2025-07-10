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

    private String password;

    private String nickname;

    private String gender;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    private Region region;

    private String profileImageUrl;
}