package sarangbang.site.challenge.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sarangbang.site.challenge.entity.Challenge;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeResponseDto {
    
    private Long id;
    private String title;
    private String location;
    private String image;
    private int participants;
    private int currentParticipants;
    private LocalDate startDate;
    private Long period;

    public ChallengeResponseDto(Challenge challenge, int currentParticipants) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.location = challenge.getRegion().getFullAddress();
        this.image = challenge.getImage();
        this.participants = challenge.getParticipants();
        this.currentParticipants = currentParticipants;
        this.startDate = challenge.getStartDate();

        // 시작일과 종료일이 모두 있을 경우 기간(일) 계산
        if (challenge.getStartDate() != null && challenge.getEndDate() != null) {
            this.period = ChronoUnit.DAYS.between(challenge.getStartDate(), challenge.getEndDate());
        } else {
            this.period = 0L; // 날짜 정보가 없을 경우 기본값 0으로 설정
        }
    }
}
