package sarangbang.site.region.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.global.entity.BaseEntity;

@Entity
@Table(name = "regions")
@NoArgsConstructor
@Getter
public class Region extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long regionId;

    @Column(length = 50, nullable = false)
    private String regionName;

    @Enumerated(EnumType.STRING)
    @Column(name = "region_type", nullable = false, length = 10)
    private RegionType regionType;

    private Long parentRegionId;

    @Column(nullable = false)
    private String fullAddress;
}