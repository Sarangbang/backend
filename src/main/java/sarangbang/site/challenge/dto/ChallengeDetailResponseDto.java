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

        // 추가적인 계산이나 변환이 필요한 값들을 처리합니다.
        this.currentParticipants = currentParticipants; // 서비스 레이어에서 계산한 현재 참여자 수를 할당합니다.
        this.challengeStatus = challenge.isStatus() ? "ACTIVE" : "INACTIVE"; // boolean 타입의 status를 클라이언트가 이해하기 쉬운 문자열로 변환합니다.

        // 연관된 엔티티(ChallengeCategory)의 정보를 DTO(CategoryDTO)로 변환합니다.
        // 이것이 엔티티를 직접 노출하지 않고 필요한 데이터만 전달하는 핵심적인 부분입니다.
        this.category = new ChallengeCategoryDTO( // 새로 만든 외부 CategoryDTO 객체를 생성합니다.
                challenge.getChallengeCategory().getCategoryId(), // 카테고리 엔티티에서 ID를 가져옵니다.
                challenge.getChallengeCategory().getCategoryName() // 카테고리 엔티티에서 이름을 가져옵니다.
        );
    }


}
