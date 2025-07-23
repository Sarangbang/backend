package sarangbang.site.chat.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import sarangbang.site.challengeapplication.event.ChallengeMemberAcceptedEvent;
import sarangbang.site.chat.dto.ChatMessageDto;
import sarangbang.site.chat.dto.Sender;
import sarangbang.site.chat.enums.MessageType;
import sarangbang.site.chat.repository.ChatRoomRepository;
import sarangbang.site.chat.service.ChatService;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.service.UserService;
import sarangbang.site.chat.entity.ChatRoom;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ChatMessageEventListener {
    private final ChatService chatService;
    private final UserService userService;
    private final ChatRoomRepository chatRoomRepository;

    @EventListener
    public void handleChallengeMemberAccepted(ChallengeMemberAcceptedEvent event) {
        Long sourceId = event.getChallengeId();
        User user = userService.getUserById(event.getUserId());
        // 1. chatRoom participants에 userId 추가
        ChatRoom chatRoom = chatRoomRepository.findBySourceId(sourceId);
        if (chatRoom != null) {
            List<String> participants = chatRoom.getParticipants();
            if (!participants.contains(user.getId())) {
                participants.add(user.getId());
                chatRoomRepository.save(chatRoom);
            }
        }
        // 2. 입장 메시지 전송
        Sender sender = new Sender(user.getId(), user.getNickname(), user.getProfileImageUrl());
        String messageContent = user.getNickname() + "님이 챌린지에 참여했습니다.";
        ChatMessageDto entryMessage = new ChatMessageDto(
            MessageType.ENTER,
            chatRoom.getRoomId(),
            sender,
            messageContent
        );
        chatService.sendMessageToRoom(chatRoom.getRoomId(), entryMessage);
    }
} 