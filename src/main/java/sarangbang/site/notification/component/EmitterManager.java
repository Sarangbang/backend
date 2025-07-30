package sarangbang.site.notification.component;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class EmitterManager {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60; // 1시간

    // 유저 ID → Emitter 리스트 (멀티 로그인 등 대비)
    private final Map<String, List<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Emitter 저장
     */
    public SseEmitter save(String userId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);

        emitters.computeIfAbsent(userId, key -> new ArrayList<>()).add(emitter);

        // 연결이 끊겼을 때 자동 삭제
        emitter.onCompletion(() -> removeEmitter(userId, emitter));
        emitter.onTimeout(() -> removeEmitter(userId, emitter));
        emitter.onError((e) -> removeEmitter(userId, emitter));

        return emitter;
    }

    /**
     * 특정 유저의 모든 Emitter 반환
     */
    public List<SseEmitter> get(String userId) {
        return emitters.getOrDefault(userId, Collections.emptyList());
    }

    /**
     * Emitter 삭제
     */
    public void removeEmitter(String userId, SseEmitter emitter) {
        List<SseEmitter> emitterList = emitters.get(userId);
        if (emitterList != null) {
            emitterList.remove(emitter);
            if (emitterList.isEmpty()) {
                emitters.remove(userId);
            }
        }
    }

    /**
     * 특정 유저에게 데이터 전송
     */
    public void send(String userId, Object data) {
        List<SseEmitter> emitterList = get(userId);

        for (SseEmitter emitter : emitterList) {
            try {
                emitter.send(SseEmitter.event().name("notification").data(data));
            } catch (IOException e) {
                removeEmitter(userId, emitter); // 실패 시 제거
            }
        }
    }
}
