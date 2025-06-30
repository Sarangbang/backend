package sarangbang.site.region.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.region.dto.RegionResponseDTO;
import sarangbang.site.region.service.RegionService;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    /* 전체 시도 조회 */
    @GetMapping("/sido")
    public ResponseEntity<List<RegionResponseDTO>> getSidoList() {
        List<RegionResponseDTO> response = regionService.findChildren(null);
        return ResponseEntity.ok(response);
    }

}