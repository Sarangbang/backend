package sarangbang.site.chat.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import sarangbang.site.chat.entity.ChatMessage;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    Slice<ChatMessage> findByRoomId(String roomId, Pageable pageable);

    long countByRoomIdAndSenderNotAndCreatedAtAfter(String roomId, String senderId, LocalDateTime createdAtAfter);

    /**
     * 특정 채팅방(roomId)에서 가장 최근에 생성된 메시지 1개를 조회합니다.
     * @param roomId 조회할 채팅방의 ID
     * @return Optional<ChatMessage>
     */
    Optional<ChatMessage> findTopByRoomIdOrderByCreatedAtDesc(String roomId);
}
