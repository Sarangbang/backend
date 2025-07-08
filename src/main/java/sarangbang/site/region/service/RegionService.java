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
            boolean parentExists = regionRepository.existsById(parentRegionId);
            if (!parentExists) {
                log.error("존재하지 않는 parentRegionId: {}", parentRegionId);
                throw new RegionNotFoundException("해당 ID의 지역을 찾을 수 없습니다");
            }
        }

        List<Region> regions = regionRepository.findByParentRegionId(parentRegionId);

        log.debug("조회된 지역 개수: {}", regions.size());

        return regions.stream()
                .map(RegionResponseDTO::from)
                .toList();
    }

    /**
     * ID로 Region 엔티티를 조회합니다. (내부 서비스용)
     * @param regionId 찾고자 하는 지역의 ID
     * @return Region 엔티티
     * @throws RegionNotFoundException 해당 ID의 지역이 없을 경우 발생
     */
    public Region findRegionById(Long regionId) {
        return regionRepository.findById(regionId)
                .orElseThrow(() -> new RegionNotFoundException("유효하지 않은 지역 ID입니다: " + regionId));
    }

}
