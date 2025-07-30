package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnreadMessageEventDto {
    private String type = "UNREAD_MESSAGE";
    private String message;
    private String roomId;
    private LocalDateTime createdAt;

    public UnreadMessageEventDto(String roomId, String message, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.message = message;
        this.createdAt = createdAt;
    }
}
