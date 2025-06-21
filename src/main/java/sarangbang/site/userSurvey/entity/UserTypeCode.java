package sarangbang.site.userSurvey.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "user_type_code")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserTypeCode extends BaseEntity {

    @Id
    @Column(name = "type_code", length = 5)
    private String typeCode;

    @Column(name = "type_name", length = 50, nullable = false)
    private String typeName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

}