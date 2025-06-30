package sarangbang.site.challenge.dto;

import lombok.Getter;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengecategory.dto.ChallengeCategoryDTO;
import java.time.LocalDate;

@Getter
public class ChallengeDetailResponseDto {
    private final Long challengeId;
    private final String title;
    private final String description;
    private final String imageUrl;
    private final String method;
    private final int maxParticipants;
    private final Long currentParticipants; // 현재 참여 인원
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final String challengeStatus;
    private final String location;
    private final ChallengeCategoryDTO category;

    /**
    * Challenge 엔티티와 현재 참여자 수를 받아 상세조회 DTO를 생성하는 생성자입니다.
    * @param challenge 데이터베이스에서 조회한 Challenge 엔티티 객체
    * @param currentParticipants ChallengeMemberRepository에서 계산한 현재 참여자 수
    * */
    public ChallengeDetailResponseDto(Challenge challenge, Long currentParticipants) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.imageUrl = challenge.getImage();
        this.method = challenge.getMethod();
        this.maxParticipants = challenge.getParticipants();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.location = challenge.getLocation();

        this.currentParticipants = currentParticipants;
        this.challengeStatus = challenge.isStatus() ? "ACTIVE" : "INACTIVE";

        this.category = new ChallengeCategoryDTO(
                challenge.getChallengeCategory().getCategoryId(),
                challenge.getChallengeCategory().getCategoryName()
        );
    }
}
