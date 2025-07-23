package sarangbang.site.challengeapplication.event;

import lombok.Getter;

@Getter
public class ChallengeMemberAcceptedEvent {
    private final Long challengeId;
    private final String userId;
    private final String userNickname;

    public ChallengeMemberAcceptedEvent(Long challengeId, String userId, String userNickname) {
        this.challengeId = challengeId;
        this.userId = userId;
        this.userNickname = userNickname;
    }
} 