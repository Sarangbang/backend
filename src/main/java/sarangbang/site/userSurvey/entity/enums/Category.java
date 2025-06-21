package sarangbang.site.userSurvey.entity.enums;

import lombok.Getter;

@Getter
public enum Category {
    PERSONALITY("personality"),
    DATING("dating"),
    LIFESTYLE("lifestyle");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}