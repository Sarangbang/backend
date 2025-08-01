package sarangbang.site.chat.entity;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import sarangbang.site.chat.enums.ChatSourceType;
import sarangbang.site.chat.enums.RoomType;

import java.time.LocalDateTime;
import java.util.List;

@Document(collection = "chat_rooms")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChatRoom {

    @Id
    private String _id; // DB 식별자

    @Indexed(unique = true)
    private String roomId; // 비즈니스 식별자

    @Enumerated(EnumType.STRING)
    private RoomType roomType; // ONE_TO_ONE, GROUP

    @Enumerated(EnumType.STRING)
    private ChatSourceType sourceType; // CHALLENGE, LOCAL_MEETING 등
    private Long sourceId;           // 챌린지 ID 또는 동네모임 ID

    private String roomName;
    private String creatorId;

    @Indexed
    private List<String> participants;
    private LocalDateTime createdAt;

    private String avatar;

    public ChatRoom(String roomId, RoomType roomType, ChatSourceType sourceType, Long sourceId, String roomName, String creatorId, List<String> participants, LocalDateTime createdAt, String avatar) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.sourceType = sourceType;
        this.sourceId = sourceId;
        this.roomName = roomName;
        this.creatorId = creatorId;
        this.participants = participants;
        this.createdAt = createdAt;
        this.avatar = avatar;
    }
}
