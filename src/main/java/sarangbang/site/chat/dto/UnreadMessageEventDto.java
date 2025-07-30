package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadMessageEventDto {
    private String type = "UNREAD_MESSAGE";
    private String roomId;

    public UnreadMessageEventDto(String roomId) {
        this.roomId = roomId;
    }
}
