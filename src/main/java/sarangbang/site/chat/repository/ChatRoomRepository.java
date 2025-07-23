package sarangbang.site.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chat.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantsContaining(String userId);

    ChatRoom findBySourceId(Long sourceId);
}
