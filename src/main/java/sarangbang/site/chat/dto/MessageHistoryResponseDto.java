package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class MessageHistoryResponseDto {

    private List<ChatMessageDto> messages;
    private boolean hasNext;
}
