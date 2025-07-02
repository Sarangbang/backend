package sarangbang.site.challengecategory.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import sarangbang.site.challengecategory.entity.ChallengeCategory;

@Data
@AllArgsConstructor
public class ChallengeCategoryDTO {
    @Schema(description = "챌린지 카테고리 ID", example = "2")
    private Long categoryId;
    @Schema(description = "챌린지 카테고리 이름", example = "학습/도서")
    private String categoryName;

    public static ChallengeCategoryDTO fromEntity(ChallengeCategory category) {
        return new ChallengeCategoryDTO(
            category.getCategoryId(),
            category.getCategoryName()
        );
    }
}
