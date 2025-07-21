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
}
