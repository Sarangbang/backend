package sarangbang.site.region.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.region.dto.RegionResponseDTO;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.exception.RegionNotFoundException;
import sarangbang.site.region.repository.RegionRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    /* 공통 - 자식 지역들 반환 */
    public List<RegionResponseDTO> findChildren(Long parentRegionId) {
        log.info("자식 지역 조회 요청. parentRegionId = {}", parentRegionId);

        if (parentRegionId != null) {
            boolean exists = regionRepository.existsById(parentRegionId);
            if (!exists) {
                log.warn("존재하지 않는 parentRegionId: {}", parentRegionId);
                throw new RegionNotFoundException("해당 ID의 지역을 찾을 수 없습니다");
            }
        }

        List<Region> regions = regionRepository.findByParentRegionId(parentRegionId);

        log.debug("조회된 지역 개수: {}", regions.size());

        return regions.stream()
                .map(RegionResponseDTO::from)
                .toList();
    }

}
