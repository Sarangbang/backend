package sarangbang.site.userSurvey.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class SurveyAnswersDto {

    private List<Integer> answers;

    public SurveyAnswersDto(List<Integer> answers) {
        this.answers = answers;
    }
}