package sarangbang.site.challengecategory.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.service.ChallengeCategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/challenge/categories")
@RequiredArgsConstructor
@Slf4j
public class ChallengeCategoryController {

    private final ChallengeCategoryService challengeCategoryService;

    /**
     * 카테고리 목록 조회 API
     */
    @GetMapping
    public ResponseEntity<List<ChallengeCategory>> getCategories() {
        try {
            List<ChallengeCategory> categories = challengeCategoryService.getAllCategories();
            return ResponseEntity.ok(categories);

        } catch (Exception e) {
            log.error("카테고리 목록 조회 실패 - 에러: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 개별 카테고리 조회 API
     */
    @GetMapping("/{categoryId}")
    public ResponseEntity<ChallengeCategory> getCategoryById(@PathVariable Long categoryId) {
        try {
            ChallengeCategory challengeCategory = challengeCategoryService.getCategoryById(categoryId);
            
            if (challengeCategory == null) {
                log.warn("카테고리를 찾을 수 없음 - ID: {}", categoryId);
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok(challengeCategory);

        } catch (Exception e) {
            log.error("카테고리 조회 실패 - ID: {}, 에러: {}", categoryId, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}