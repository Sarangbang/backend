package sarangbang.site.challenge.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challengecategory.dto.ChallengeCategoryDTO;
import java.time.LocalDate;

@Data
public class ChallengeDetailResponseDto {
    @Schema(description = "챌린지 고유 Id", example = "1")
    private final Long challengeId;
    @Schema(description = "챌린지 제목", example = "JPA 정복 스터디")
    private final String title;
    @Schema(description = "챌린지 상세 설명", example = "김영한님의 '자바 ORM 표준 JPA 프로그래밍' 책을 함께 완독하는 스터디입니다.")
    private final String description;
    @Schema(description = "챌린지 대표 이미지 URL", example = "https://example.com/images/jpa_study.jpg")
    private final String imageUrl;
    @Schema(description = "챌린지 인증 방법", example = "이미지")
    private final String method;
    @Schema(description = "최대 참여 가능 인원", example = "10")
    private final int maxParticipants;
    @Schema(description = "현재 참여 인원", example = "5")
    private final int currentParticipants;
    @Schema(description = "챌린지 시작일", example = "2025-07-01")
    private final LocalDate startDate;
    @Schema(description = "챌린지 종료일", example = "2025-07-31")
    private final LocalDate endDate;
    @Schema(description = "챌린지 진행 상태", example = "ACTIVE")
    private final String challengeStatus;
    @Schema(description = "챌린지 진행 지역", example = "서울특별시")
    private final String location;
    @Schema(description = "챌린지 카테고리 정보")
    private final ChallengeCategoryDTO category;

    /**
    * Challenge 엔티티와 현재 참여자 수를 받아 상세조회 DTO를 생성하는 생성자입니다.
    * @param challenge 데이터베이스에서 조회한 Challenge 엔티티 객체
    * @param currentParticipants ChallengeMemberRepository에서 계산한 현재 참여자 수
    * */
    public ChallengeDetailResponseDto(Challenge challenge, String imageUrl, int currentParticipants) {
        this.challengeId = challenge.getId();
        this.title = challenge.getTitle();
        this.description = challenge.getDescription();
        this.imageUrl = imageUrl;
        this.method = challenge.getMethod();
        this.maxParticipants = challenge.getParticipants();
        this.startDate = challenge.getStartDate();
        this.endDate = challenge.getEndDate();
        this.location = challenge.getRegion().getFullAddress();

        this.currentParticipants = currentParticipants;
        this.challengeStatus = challenge.isStatus() ? "ACTIVE" : "INACTIVE";

        this.category = new ChallengeCategoryDTO(
                challenge.getChallengeCategory().getCategoryId(),
                challenge.getChallengeCategory().getCategoryName(),
                challenge.getChallengeCategory().getChallengeImageUrl()
        );

    }
}
