package sarangbang.site.userSurvey.entity.enums;

import lombok.Getter;

@Getter
public enum Category {
    PERSONALITY("성격"),
    DATING("연애"),
    LIFESTYLE("라이프스타일");

    private final String displayName;

    Category(String displayName) {
        this.displayName = displayName;
    }
}