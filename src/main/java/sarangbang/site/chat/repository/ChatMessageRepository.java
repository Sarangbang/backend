package sarangbang.site.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chat.entity.ChatMessage;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);
}
