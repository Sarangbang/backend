package sarangbang.site.region.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.region.entity.Region;

public interface RegionRepository extends JpaRepository<Region, Long> {
}
