package sarangbang.site.challengeverification.enums;

import lombok.Getter;

@Getter
public enum ChallengeVerificationStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED");

    private final String name;

    ChallengeVerificationStatus(String name) {
        this.name = name;
    }
}