package sarangbang.site.userSurvey.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;
import sarangbang.site.userSurvey.dto.SurveyAnswersDto;
import sarangbang.site.userSurvey.service.UserSurveyService;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class UserSurveyController {

    private final UserSurveyService userSurveyService;
    private final UserRepository userRepository;

    /**
     * 설문조사 답변 제출 API
     * 프론트엔드에서 9개 답변을 받아서 DB에 저장하고 결과 반환
     *
     * 요청 예시:
     * POST /api/survey/submit?userId=user123
     * {
     *   "answers": [3, 5, 2, 4, 1, 3, 5, 2, 4]
     * }
     *
     * 응답 예시:
     * {
     *   "resultId": 1,
     *   "typeCode": "H-M-L",
     *   "typeName": "사랑에 빠진 강아지",
     *   "percentage": 85
     * }
     */
    @PostMapping("/submit")
    public ResponseEntity<String> submitSurvey(
            @RequestBody SurveyAnswersDto answersDto,
            @RequestParam String userId) {

        // 기존 User 조회 (없으면 첫 번째 User 사용)
        User user = userRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("User가 존재하지 않습니다"));

        // Service에서 설문 답변 저장
        userSurveyService.saveSurveyAnswers(user, answersDto);

        return ResponseEntity.ok("설문 답변이 저장되었습니다.");
    }


}