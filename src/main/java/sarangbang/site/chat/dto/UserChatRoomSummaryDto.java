package sarangbang.site.chat.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserChatRoomSummaryDto {

    private String roomId;
    private String roomName;
    private String creatorId;
    private List<String> participants;
    private Instant createdAt;
    private String avatar;
    private Long unreadCount;

    public UserChatRoomSummaryDto(String roomId, String roomName, String creatorId, List<String> participants, Instant createdAt, String avatar) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.creatorId = creatorId;
        this.participants = participants;
        this.createdAt = createdAt;
        this.avatar = avatar;
    }
}
