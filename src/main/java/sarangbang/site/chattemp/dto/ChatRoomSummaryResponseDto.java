package sarangbang.site.chattemp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ChatRoomSummaryResponseDto {

    private String roomId;
    private String roomName;
    private String creatorId;
    private List<String> participants;
    private Instant createdAt;
}
