package sarangbang.site.challenge.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.challenge.dto.ChallengeDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sarangbang.site.challenge.dto.ChallengeDetailResponseDto;
import sarangbang.site.challenge.dto.ChallengePopularityResponseDTO;
import sarangbang.site.challenge.dto.ChallengeResponseDto;

import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;
import sarangbang.site.challengemember.service.ChallengeMemberService;

import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.file.enums.ImageType;
import sarangbang.site.file.service.FileStorageService;
import sarangbang.site.file.service.ImageSaveFactory;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.service.RegionService;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final ChallengeMemberService challengeMemberService;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final RegionService regionService;
    private final ImageSaveFactory imageSaveFactory;
    private final FileStorageService fileStorageService;

    // 챌린지 등록
    @Transactional
    public ChallengeDTO saveChallenge(ChallengeDTO dto, String userId, MultipartFile imageFile) {

        ChallengeCategory category = challengeCategoryRepository.findChallengeCategoryByCategoryId(dto.getCategoryId());
        log.debug("챌린지 카테고리 정보 : {}", category.getCategoryName());

        Region region = regionService.findRegionById(dto.getRegionId());

        Challenge challenge = new Challenge(
                region,
                dto.getTitle(),
                dto.getDescription(),
                dto.getParticipants(),
                dto.getMethod(),
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getImage(),
                true,
                category
        );

        challengeRepository.save(challenge);
        if (imageFile != null) {
            String imageUrl = imageSaveFactory.getImageUploadService(imageFile, ImageType.CHALLENGE, challenge.getId());
            challenge.changeImage(imageUrl);
        }
        challengeMemberService.saveChallengeOwner(userId, challenge.getId());

        ChallengeDTO challengeDTO = new ChallengeDTO(challenge.getRegion().getRegionId(), challenge.getTitle(), challenge.getDescription(),
                challenge.getParticipants(), challenge.getMethod(), challenge.getStartDate(), challenge.getEndDate(),
                fileStorageService.generatePresignedUrl(challenge.getImage(), Duration.ofMinutes(10)) , challenge.isStatus(), challenge.getChallengeCategory().getCategoryId());

        return challengeDTO;
    }

    /**
     * 전체 챌린지 목록 조회
     */
    public Page<ChallengeResponseDto> getAllChallenges(Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.findAllByStatus(true, pageable);
        List<ChallengeResponseDto> responseDtos = new ArrayList<>();

        for (Challenge challenge : challenges) {
            int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
            responseDtos.add(new ChallengeResponseDto(challenge, currentParticipants,
                    fileStorageService.generatePresignedUrl(challenge.getImage(), Duration.ofMinutes(10))));
        }

        PageImpl<ChallengeResponseDto> responseDtoPage = new PageImpl<>(responseDtos, challenges.getPageable(), challenges.getTotalElements());
        return responseDtoPage;
    }

    /**
     * 카테고리별 챌린지 목록 조회
     */
    public Page<ChallengeResponseDto> getChallengesByCategoryId(Long categoryId, Pageable pageable) {
        Page<Challenge> challenges = challengeRepository.findByChallengeCategory_CategoryIdAndStatus(categoryId, true, pageable);
        List<ChallengeResponseDto> responseDtos = new ArrayList<>();

        for (Challenge challenge : challenges) {
            int currentParticipants = challengeMemberRepository.countByChallengeId(challenge.getId());
            responseDtos.add(new ChallengeResponseDto(challenge, currentParticipants,
                    fileStorageService.generatePresignedUrl(challenge.getImage(), Duration.ofMinutes(10))));
        }

        PageImpl<ChallengeResponseDto> responseDtoPage = new PageImpl<>(responseDtos, pageable, challenges.getTotalElements());
        return responseDtoPage;
    }

    // id값으로 챌린지 조회
    public Challenge getChallengeById(Long challengeId) {
        Challenge challenge = challengeRepository.findChallengeById(challengeId);
        if(challenge == null) {
            throw new IllegalArgumentException("챌린지가 존재하지 않습니다.");
        }
        return challenge;
    }


    /**
     * 챌린지 상세 정보 조회
     * @param challengeId 조회할 챌린지의 ID
     * @return 챌린지 상세 정보를 담은 DTO
     */
    public ChallengeDetailResponseDto getChallengeDetails(Long challengeId) {
        Challenge challenge = getChallengeById(challengeId);

        //현재 참여자 수를 조회
        int currentParticipants = challengeMemberRepository.countByChallengeId(challengeId);

        //엔티티와 참여자 수를 DTO 생성자에 넘겨 변환 후 반환
        return new ChallengeDetailResponseDto(challenge, currentParticipants);
    }

    // 챌린지 인기순 조회
    public List<ChallengePopularityResponseDTO> getChallengePopularity() {
        return challengeRepository.findChallengesByPopularity();
    }
}
