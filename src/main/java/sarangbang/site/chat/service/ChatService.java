package sarangbang.site.chat.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import sarangbang.site.chat.dto.ChatMessageDto;
import sarangbang.site.chat.entity.ChatMessage;
import sarangbang.site.chat.repository.ChatMessageRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessageSendingOperations messagingTemplate;

    /**
     * 메시지를 DB에 저장하고, 해당 채팅방을 구독하는 클라이언트들에게 메시지를 전송
     * @param messageDto 수신한 채팅 메시지 DTO
     * */
    public void saveAndSendMessage(ChatMessageDto messageDto) {
        // 1. DTO를 Document(DB 저장용 모델)로 변환
        ChatMessage chatMessage = new ChatMessage(
                messageDto.getRoomId(),
                messageDto.getSender(),
                messageDto.getContent(),
                Instant.now()
        );

        // 2. 메시지를 MongoDB에 저장
        chatMessageRepository.save(chatMessage);

        // 3. 목적지 경로를 동적으로 설정
        String destination = "/topic/chat/room/" + chatMessage.getRoomId();
        log.info("Sending message to " + destination);

        // 4. WebSocket 브로커를 통해 메시지를 해당 목적지로 전송
        //    구독 중인 모든 클라이언트가 이 메시지를 수신하게 됨
        messagingTemplate.convertAndSend(destination, chatMessage);
    }
}
