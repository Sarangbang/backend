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

    private String profileImageUrl;

    private Integer age;

    private String gender;

    private String region;

    private String religion;

    private Integer salary;

    private String job;

    @Column(columnDefinition = "TEXT")
    private String psychologicalResult; // JSON 문자열 저장

    private Double heightCm;

    private Double weightKg;

    public User(String profileImageUrl, Integer age, String gender, String region, 
                String religion, Integer salary, String job, String psychologicalResult, 
                Double heightCm, Double weightKg) {
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.gender = gender;
        this.region = region;
        this.religion = religion;
        this.salary = salary;
        this.job = job;
        this.psychologicalResult = psychologicalResult;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
    }
}
