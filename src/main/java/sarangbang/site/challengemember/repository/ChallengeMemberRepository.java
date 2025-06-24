package sarangbang.site.challengemember.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;

public interface ChallengeMemberRepository extends JpaRepository<ChallengeMember, Integer> {
}