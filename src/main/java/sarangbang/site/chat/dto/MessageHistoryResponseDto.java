package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import sarangbang.site.chat.entity.ChatMessage;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageHistoryResponseDto {

    private List<ChatMessage> messages;
    private boolean hasNext;
}
