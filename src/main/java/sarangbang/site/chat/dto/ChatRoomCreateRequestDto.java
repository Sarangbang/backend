package sarangbang.site.chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ChatRoomCreateRequestDto {

    private String roomName;
    private String creatorId;
    private List<String> participants;
}
