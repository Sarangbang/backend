package sarangbang.site.challengemember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.user.entity.User;

import java.util.Optional;

import java.util.List;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Long> {

    int countByChallengeId(Long challengeId);

    Optional<ChallengeMember> findChallengeMemberByUser_IdAndChallenge_Id(String userId, Long challengeId);
    List<ChallengeMember> findByChallengeId(Long challengeId);
    boolean existsByChallengeIdAndUser(Long challengeId, User user);
    List<ChallengeMember> findByUser_IdAndRole(String userId, String role);
    List<ChallengeMember> findByUser_Id(String userId);
    ChallengeMember findByChallenge_IdAndRoleIsContaining(Long challengeId, String role);
}