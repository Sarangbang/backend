package sarangbang.site.chat.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import sarangbang.site.challenge.event.ChallengeCreatedEvent;
import sarangbang.site.chat.dto.ChatRoomCreateRequestDto;
import sarangbang.site.chat.service.ChatRoomService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ChatRoomChallengeListener {

    private final ChatRoomService chatRoomService;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleChallengeCreatedEvent(ChallengeCreatedEvent event) {
        log.info("챌린지 생성 이벤트 수신. 챌린지 ID: {}" + event.getChallengeId());

        List<String> participantList = new ArrayList<>();
        participantList.add(event.getCreatorId());

        ChatRoomCreateRequestDto requestDto = new ChatRoomCreateRequestDto(
                event.getRoomType(),
                event.getSourceType(),
                event.getChallengeId(),
                event.getChallengeTitle(),
                event.getCreatorId(),
                participantList,
                event.getChallengeImageUrl()
        );

        chatRoomService.createRoom(
                requestDto,
                event.getCreatorId()
        );
    }
}
