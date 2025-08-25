package sarangbang.site.ai.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.service.ChallengeService;


@Service
@RequiredArgsConstructor
public class MotivationalMessageService {

    private final GeminiApiClient geminiApiClient;
    private final ChallengeService challengeService;

		// 비동기 처리
		// public Mono<String> generateMotivationalMessage(Long challengeId) {
		// 	return Mono.fromCallable(() -> challengeService.getChallengeById(challengeId))
		// 					.flatMap(this::createTextGeneration);
		// }

    public String generateMotivationalMessage(Long challengeId) {
        Challenge challenge = challengeService.getChallengeById(challengeId);
        return createTextGeneration(challenge);
    }

    private String createTextGeneration(Challenge challenge) {
        String prompt = createPrompt(challenge);
        return geminiApiClient.generateText(prompt).block();
    }

    private String createPrompt(Challenge challenge) {
        return String.format(
                """
                너는 사용자에게 동기부여 메시지를 생성해주는 AI 비서야. 다음 챌린지에 참여하는 사용자를 위해 짧고 힘이 나는 동기부여 메시지를 한국어로 생성해줘.
                응답은 동기부여 메시지만 포함하고, 다른 설명은 제외해줘.
                
                챌린지 제목: %s
                챌린지 설명: %s
                """,
                challenge.getTitle(),
                challenge.getDescription()
        );
    }
}
