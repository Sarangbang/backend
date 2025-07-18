package sarangbang.site.challengecategory.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengecategory.dto.ChallengeCategoryDTO;
import sarangbang.site.challengecategory.service.ChallengeCategoryService;

import java.util.List;

@Tag(name = "Category", description = "카테고리 조회 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Slf4j
public class ChallengeCategoryController {

    private final ChallengeCategoryService challengeCategoryService;

    /**
     * 카테고리 목록 조회 API
     */
    @Operation(summary = "전체 카테고리 목록 조회", description = "카테고리 목록을 전체 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공")
    })
    @GetMapping
    public ResponseEntity<List<ChallengeCategoryDTO>> getCategories() {
        List<ChallengeCategoryDTO> categories = challengeCategoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * 개별 카테고리 조회 API
     */
    @Operation(summary = "단일 카테고리 조회", description = "전달받은 카테고리 Id를 기준으로 해당 단일 챌린지를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회 성공"),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 카테고리 ID")
    })
    @GetMapping("/{categoryId}")
    public ResponseEntity<ChallengeCategoryDTO> getCategoryById(@PathVariable Long categoryId) {

        ChallengeCategoryDTO challengeCategory = challengeCategoryService.getCategoryById(categoryId);
        
        if (challengeCategory == null) {
            log.warn("카테고리를 찾을 수 없음 - ID: {}", categoryId);
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(challengeCategory);

    }
}