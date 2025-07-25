package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import sarangbang.site.chat.enums.MessageType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto {

    // 메시지 유형 (입장, 퇴장, 대화)
    private MessageType type;

    // 채팅방 ID
    private String roomId;

    // 메시지를 보낸 사람
    private Sender sender;

    // 메시지 내용
    private String message;

    private LocalDateTime createdAt;

    private int unreadCount;

    public ChatMessageDto(MessageType type, String roomId, Sender sender, String message, LocalDateTime createdAt) {
        this.type = type;
        this.roomId = roomId;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }
}
