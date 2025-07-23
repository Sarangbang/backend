package sarangbang.site.challenge.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sarangbang.site.chat.enums.ChatSourceType;
import sarangbang.site.chat.enums.RoomType;

@Getter
@AllArgsConstructor
public class ChallengeCreatedEvent {

    private final Long challengeId;
    private final String challengeTitle;
    private final String creatorId;
    private final RoomType roomType;
    private final ChatSourceType sourceType;
    private final String challengeImageUrl;
}
