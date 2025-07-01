package sarangbang.site.region.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.region.dto.RegionResponseDTO;
import sarangbang.site.region.service.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /* 전체 시도 조회 */
    @GetMapping("/regions/sido")
    public ResponseEntity<List<RegionResponseDTO>> getSidoList() {
        List<RegionResponseDTO> response = regionService.findChildren(null);
        return ResponseEntity.ok(response);
    }

    /* 부모 지역 기준 자식 지역 조회 */
    @GetMapping("/region")
    public ResponseEntity<List<RegionResponseDTO>> getChildren(@RequestParam Long regionId) {
        List<RegionResponseDTO> response = regionService.findChildren(regionId);
        return ResponseEntity.ok(response);
    }

}