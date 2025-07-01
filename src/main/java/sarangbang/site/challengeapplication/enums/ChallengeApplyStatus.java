package sarangbang.site.challengeapplication.enums;

import lombok.Getter;

@Getter
public enum ChallengeApplyStatus {
    PENDING("승인대기"),
    APPROVED("승인"),
    REJECTED("거절");

    private final String displayName;

    ChallengeApplyStatus(String displayName) {
        this.displayName = displayName;
    }
}
