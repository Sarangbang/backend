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
    private String roomId;
    private String roomName;
    private String creatorId;
    private List<String> participants;
    private Instant createdAt;
}
