package sarangbang.site.chat.dto;

import lombok.*;
import sarangbang.site.chat.enums.ChatSourceType;
import sarangbang.site.chat.enums.RoomType;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoomCreateRequestDto {

    private RoomType roomType;
    private ChatSourceType sourceType;
    private Long sourceId;
    private String roomName;
    private String creatorId;
    private List<String> participants;
    private String challengeImageUrl;
}
