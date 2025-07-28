package sarangbang.site.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chat.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByRoomIdOrderByCreatedAtAsc(String roomId);

    Slice<ChatMessage> findByRoomId(String roomId, Pageable pageable);

    long countByRoomIdAndSender_UserIdNotAndCreatedAtAfter(String roomId, String sender_UserId, LocalDateTime createdAtAfter);


}
