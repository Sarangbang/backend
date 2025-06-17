package sarangbang.site.chat.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    private String _id; // DB 식별자
    private String roomId; // 비즈니스 식별자
    private String roomName;
    private String creatorId;
    private List<String> participants;
    private Instant createdAt;

    public ChatRoom(String roomId, String roomName, String creatorId, List<String> participants, Instant createdAt) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.creatorId = creatorId;
        this.participants = participants;
        this.createdAt = createdAt;
    }
}
