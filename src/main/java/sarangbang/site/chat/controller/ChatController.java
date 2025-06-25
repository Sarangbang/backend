package sarangbang.site.chat.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import sarangbang.site.chat.dto.ChatMessageDto;
import sarangbang.site.chat.entity.ChatMessage;
import sarangbang.site.chat.service.ChatService;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    /**
     * 클라이언트가 메시지를 보내는 경로: /app/chat.sendMessage
     * 서버는 이 메시지를 받아서 /topic/public 주소를 구독하는 모든 클라이언트에게 전송
     * */
    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public void sendMessage(@Payload ChatMessageDto chatMessageDto) {
        chatService.saveAndSendMessage(chatMessageDto);
    }

    /**
     * 클라이언트가 사용자를 추가(입장)할 때 사용하는 경로: /app/chat.addUser
     * 입장 메시지를 /topic/public 주소를 구독하는 모든 클라이언트에게 전송
     * */
    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public ChatMessage addUser(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
        headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
        return chatMessage;
    }
}
