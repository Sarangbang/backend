package sarangbang.site.challengeapplication.enums;

public enum Status {
    PENDING("승인대기"),
    APPROVED("승인"),
    REJECTED("거절");

    private String displayName;

    Status(String displayName) {
        this.displayName = displayName;
    }
}
