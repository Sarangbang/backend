package sarangbang.site.challengeapplication.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import sarangbang.site.challengeapplication.dto.ChangeChallengeAppDTO;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.challengemember.service.ChallengeMemberService;

@Service
@RequiredArgsConstructor
public class ChallengeApplicationService {

    private final ChallengeApplicationRepository challengeApplicationRepository;
    private final ChallengeMemberService challengeMemberService;
    private final ChallengeMemberRepository challengeMemberRepository;

    // 챌린지 신청 수락/거부
    public ChangeChallengeAppDTO changeApplicationStatus(Long appId, ChangeChallengeAppDTO dto, String ownerId) throws EntityNotFoundException {

        ChallengeApplication app = challengeApplicationRepository.findChallengeApplicationById(appId);
        ChallengeMember member = challengeMemberRepository.findChallengeMemberByUser_IdAndChallenge_Id(ownerId, app.getChallenge().getId()); // 특정 챌린지의 id 에서 member 조회

        if(app.getStatus().equals("approved") || app.getStatus().equals("rejected")){
            throw new IllegalStateException("이미 처리된 신청입니다.");

        }
        if(member.getRole().equals("owner")) {

            if(dto.getStatus().equals("거부")) {
                app.updateAppStatus("rejected");
                app.updateAppComment(dto.getComment());
            } else {
                app.updateAppStatus("approved");
                app.updateAppComment(dto.getComment());
                challengeMemberService.saveChallengeMember(app.getUser().getId(), appId);
            }
            challengeApplicationRepository.save(app);

            ChangeChallengeAppDTO changeApp = new ChangeChallengeAppDTO(app.getStatus(), app.getComment());
            return changeApp;

        } else throw new IllegalArgumentException("챌린지 방장의 권한이 없습니다.");

    }
}
