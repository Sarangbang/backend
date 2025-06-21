package sarangbang.site.userSurvey.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.userSurvey.dto.SurveyAnswersDto;
import sarangbang.site.userSurvey.service.UserSurveyService;

@RestController
@RequestMapping("/api/survey")
@RequiredArgsConstructor
@Slf4j
public class UserSurveyController {

    private final UserSurveyService userSurveyService;

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

        log.info("설문 답변 제출 요청 - userId: {}, 답변 개수: {}", userId, answersDto.getAnswers().size());

        try {
            // Service에 userId만 넘김
            userSurveyService.saveSurveyAnswers(userId, answersDto);

            log.info("설문 답변 저장 완료 - userId: {}", userId);
            return ResponseEntity.ok("설문 답변이 저장되었습니다.");

        } catch (Exception e) {
            log.error("설문 답변 저장 실패 - userId: {}, 에러: {}", userId, e.getMessage(), e);
            return ResponseEntity.internalServerError().body("설문 답변 저장 중 오류가 발생했습니다.");
        }
    }

}