package sarangbang.site.chat.enums;

public enum MessageType {
    ENTER,  // 사용자가 채팅방에 처음 입장
    LEAVE,  // 사용자가 채팅방에서 처음 퇴장
    TALK,    // 일반 대화 메시지
    RE_ENTER, // 사용자가 채팅방에 재입장
    RE_LEAVE, // 사용자가 채팅방에서 재퇴장
    UNREAD_MESSAGE
}
