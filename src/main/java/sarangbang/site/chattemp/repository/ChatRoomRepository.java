package sarangbang.site.chattemp.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chattemp.entity.ChatRoom;

import java.util.List;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
    List<ChatRoom> findByParticipantsContaining(String userId);
}
