package sarangbang.site.region.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sarangbang.site.region.entity.Region;

@Getter
@AllArgsConstructor
public class RegionResponseDTO {

    private Long regionId;
    private String regionName;
    private String regionType;

    public static RegionResponseDTO from(Region region) {
        return new RegionResponseDTO(
            region.getRegionId(),
            region.getRegionName(),
            region.getRegionType().name()
        );
    }
}
