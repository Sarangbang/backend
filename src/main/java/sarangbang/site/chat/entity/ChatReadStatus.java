package sarangbang.site.chat.entity;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Document(collection = "chat_read_status")
@CompoundIndex(name = "user_room_idx", def = "{'userID': 1, 'roomId': 1}", unique = true)
public class ChatReadStatus {

    @Id
    private String id;

    private String userId;

    private String roomId;

    private Instant lastReadAt;

    public ChatReadStatus(String userId, String roomId) {
        this.userId = userId;
        this.roomId = roomId;
    }

    public void updateLastReadAt() {
        this.lastReadAt = Instant.now();
    }
}
