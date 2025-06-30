package sarangbang.site.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.region.entity.Region;

import java.util.List;
import java.util.Optional;

public interface RegionRepository extends JpaRepository<Region, Long> {

    /* 부모Id 기준 조회 */
    List<Region> findByParentRegionId(Long parentRegionId);

}
