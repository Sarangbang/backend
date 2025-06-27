package sarangbang.site.challengeapplication.service;

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
    public ChangeChallengeAppDTO changeApplicationStatus(Long appId, ChangeChallengeAppDTO dto, String ownerId) {

        ChallengeApplication app = challengeApplicationRepository.findChallengeApplicationById(appId);
        ChallengeMember member = challengeMemberRepository.findChallengeMemberByUser_Id(ownerId);

        if(member.getRole().equals("owner")) {

            if(dto.getStatus().equals("거부")) {
                app.setStatus("rejected");
                app.setComment(dto.getComment());
            } else {
                app.setStatus("approved");
                app.setComment(dto.getComment());
                challengeMemberService.saveChallengeMember(app.getUser().getId(), appId);
            }
            challengeApplicationRepository.save(app);

            ChangeChallengeAppDTO changeApp = new ChangeChallengeAppDTO(app.getStatus(), app.getComment());
            return changeApp;

        } else throw new IllegalArgumentException("챌린지 방장의 권한이 없습니다.");

    }
}
