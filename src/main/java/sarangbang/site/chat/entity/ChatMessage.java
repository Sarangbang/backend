package sarangbang.site.chat.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import sarangbang.site.chat.dto.Sender;
import sarangbang.site.chat.enums.MessageType;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Document(collection = "chat_messages")
@AllArgsConstructor
public class ChatMessage {

    @Id
    private String _id;

    @Indexed
    private String roomId;

    private MessageType type;

    private Sender sender;

    private String message;

    private Instant createdAt;

    public ChatMessage(String roomId, MessageType type, Sender sender, String message) {
        this.roomId = roomId;
        this.type = type;
        this.sender = sender;
        this.message = message;
        this.createdAt = Instant.now();
    }
}
