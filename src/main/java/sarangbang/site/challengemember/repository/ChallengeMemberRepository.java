package sarangbang.site.challengemember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;

import java.util.Optional;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    Long countByChallengeId(Long challengeId);

    Optional<ChallengeMember> findChallengeMemberByUser_IdAndChallenge_Id(String userId, Long challengeId);
}