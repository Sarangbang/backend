package sarangbang.site.region.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import sarangbang.site.region.entity.Region;

@Schema(description = "지역 조회 응답 DTO")
@Getter
@AllArgsConstructor
public class RegionResponseDTO {

    @Schema(description = "지역 ID", example = "123")
    private Long regionId;

    @Schema(description = "지역 이름", example = "강남구")
    private String regionName;

    @Schema(description = "지역 타입 (SIDO / SIGUNGU / DONG)", example = "SIGUNGU")
    private String regionType;

    @Schema(description = "전체 주소", example = "서울특별시 강남구 역삼동")
    private String fullAddress;

    public static RegionResponseDTO from(Region region) {
        return new RegionResponseDTO(
            region.getRegionId(),
            region.getRegionName(),
            region.getRegionType().name(),
            region.getFullAddress()
        );
    }
}
