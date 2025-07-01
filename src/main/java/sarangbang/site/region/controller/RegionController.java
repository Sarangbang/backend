package sarangbang.site.region.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.region.dto.RegionResponseDTO;
import sarangbang.site.region.service.RegionService;

import java.util.List;

@Tag(name = "Region", description = "지역 조회 API")
@RestController
@RequestMapping("/api")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /* 전체 시도 조회 */
    @Operation(summary = "전체 시도 조회", description = "상위 지역(시도)을 전체 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping("/regions/sido")
    public ResponseEntity<List<RegionResponseDTO>> getSidoList() {
        List<RegionResponseDTO> response = regionService.findChildren(null);
        return ResponseEntity.ok(response);
    }

    /* 부모 지역 기준 자식 지역 조회 */
    @Operation(summary = "하위 지역 조회", description = "부모 지역 ID를 기준으로 하위 지역(시군구/동)을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 지역 ID")
    })
    @GetMapping("/region")
    public ResponseEntity<List<RegionResponseDTO>> getChildren(
            @Parameter(name = "regionId", description = "부모 지역의 ID", required = true)
            @RequestParam Long regionId
    ) {
        List<RegionResponseDTO> response = regionService.findChildren(regionId);
        return ResponseEntity.ok(response);
    }
}