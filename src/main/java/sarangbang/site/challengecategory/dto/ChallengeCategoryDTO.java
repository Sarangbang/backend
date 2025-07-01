package sarangbang.site.challengecategory.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import sarangbang.site.challengecategory.entity.ChallengeCategory;

@Data
@AllArgsConstructor
public class ChallengeCategoryDTO {
    private Long categoryId;

    private String categoryName;

    public static ChallengeCategoryDTO fromEntity(ChallengeCategory category) {
        return new ChallengeCategoryDTO(
            category.getCategoryId(),
            category.getCategoryName()
        );
    }
}
