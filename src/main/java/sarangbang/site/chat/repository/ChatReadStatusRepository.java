package sarangbang.site.chat.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chat.entity.ChatReadStatus;

import java.util.List;
import java.util.Optional;

public interface ChatReadStatusRepository extends MongoRepository<ChatReadStatus, String> {

    Optional<ChatReadStatus> findByUserIdAndRoomId(String userId, String roomId);

    List<ChatReadStatus> findByUserId(String userId);

    List<ChatReadStatus> findByRoomId(String roomId);
}
