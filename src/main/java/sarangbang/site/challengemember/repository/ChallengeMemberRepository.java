package sarangbang.site.challengemember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;

import java.util.List;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    Long countByChallengeId(Long challengeId);
    List<ChallengeMember> findByChallengeId(Long challengeId);
}