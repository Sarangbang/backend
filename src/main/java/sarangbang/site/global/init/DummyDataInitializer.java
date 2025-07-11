package sarangbang.site.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengeapplication.entity.ChallengeApplication;
import sarangbang.site.challengeapplication.enums.ChallengeApplyStatus;
import sarangbang.site.challengeapplication.repository.ChallengeApplicationRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;
import sarangbang.site.challengemember.entity.ChallengeMember;
import sarangbang.site.challengemember.repository.ChallengeMemberRepository;
import sarangbang.site.challengeverification.entity.ChallengeVerification;
import sarangbang.site.challengeverification.enums.ChallengeVerificationStatus;
import sarangbang.site.challengeverification.repository.ChallengeVerificationRepository;
import sarangbang.site.chat.entity.ChatRoom;
import sarangbang.site.chat.repository.ChatRoomRepository;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.entity.RegionType;
import sarangbang.site.region.repository.RegionRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
@RequiredArgsConstructor
@Profile("dev")
@ConditionalOnProperty(
        name = "spring.jpa.hibernate.ddl-auto", // 2-1. 검사할 속성의 이름을 지정합니다.
        havingValue = "create" // 2-2. 이 속성이 "create"라는 값을 가질 때만 이 컴포넌트를 활성화합니다.
)
public class DummyDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final ChallengeRepository challengeRepository;
    private final ChallengeApplicationRepository challengeApplicationRepository;
    private final ChallengeMemberRepository challengeMemberRepository;
    private final ChallengeVerificationRepository challengeVerificationRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final RegionRepository regionRepository;
    private final PasswordEncoder passwordEncoder;

    private final List<User> users = new ArrayList<>();
    private final List<ChallengeCategory> categories = new ArrayList<>();
    private final List<Challenge> challenges = new ArrayList<>();
    private final List<Region> regions = new ArrayList<>();
    private final Random random = new Random();

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        createRegions();
        createUsers();
        createChallengeCategories();
        createChallenges();
        createChallengeApplicationsAndMembers();
        createChallengeVerifications();
    }

    private void createRegions() {
        // 서울특별시
        Region seoul = regionRepository.save(new Region(null, "서울특별시", RegionType.SIDO, null, "서울특별시"));
        Region gangnam = new Region(null, "강남구", RegionType.SIGUNGU, seoul.getRegionId(), "서울특별시 강남구");
        Region seocho = new Region(null, "서초구", RegionType.SIGUNGU, seoul.getRegionId(), "서울특별시 서초구");
        Region jongno = new Region(null, "종로구", RegionType.SIGUNGU, seoul.getRegionId(), "서울특별시 종로구");
        Region mapo = new Region(null, "마포구", RegionType.SIGUNGU, seoul.getRegionId(), "서울특별시 마포구");
        regions.add(seoul);
        regions.addAll(regionRepository.saveAll(Arrays.asList(gangnam, seocho, jongno, mapo)));

        // 부산광역시
        Region busan = regionRepository.save(new Region(null, "부산광역시", RegionType.SIDO, null, "부산광역시"));
        Region haeundae = new Region(null, "해운대구", RegionType.SIGUNGU, busan.getRegionId(), "부산광역시 해운대구");
        Region suyeong = new Region(null, "수영구", RegionType.SIGUNGU, busan.getRegionId(), "부산광역시 수영구");
        Region jingu = new Region(null, "부산진구", RegionType.SIGUNGU, busan.getRegionId(), "부산광역시 부산진구");
        Region geumjeong = new Region(null, "금정구", RegionType.SIGUNGU, busan.getRegionId(), "부산광역시 금정구");
        regions.add(busan);
        regions.addAll(regionRepository.saveAll(Arrays.asList(haeundae, suyeong, jingu, geumjeong)));
    }

    private void createUsers() {
        Region region = regions.get(1); // 강남구

        User user1 = new User("user01", "user01@test.com", passwordEncoder.encode("12345678"), "성실한챌린저", "MALE", region, null);
        User user2 = new User("user02", "user02@test.com", passwordEncoder.encode("12345678"), "열심챌린저", "FEMALE", region, null);
        User user3 = new User("user03", "user03@test.com", passwordEncoder.encode("12345678"), "가끔챌린저", "MALE", region, null);
        User user4 = new User("user04", "user04@test.com", passwordEncoder.encode("12345678"), "구경꾼", "FEMALE", region, null);
        User user5 = new User("user05", "user05@test.com", passwordEncoder.encode("12345678"), "탈퇴한사용자", "MALE", region, null);
        User user6 = new User("user06", "user06@test.com", passwordEncoder.encode("12345678"), "탈퇴한사용자", "MALE", region, null);
        User user7 = new User("testuser", "testuser@example.com@test.com", passwordEncoder.encode("12345678"), "탈퇴한사용자", "MALE", region, null);
        user5.inactive(); // 탈퇴 처리

        users.addAll(userRepository.saveAll(Arrays.asList(user1, user2, user3, user4, user5, user6, user7)));
    }

    private void createChallengeCategories() {
        ChallengeCategory cat1 = new ChallengeCategory("운동", "https://picsum.photos/200/300");
        ChallengeCategory cat2 = new ChallengeCategory("스터디", "https://picsum.photos/200/300");
        ChallengeCategory cat3 = new ChallengeCategory("생활습관", "https://picsum.photos/200/300");
        ChallengeCategory cat4 = new ChallengeCategory("취미", "https://picsum.photos/200/300");

        categories.addAll(challengeCategoryRepository.saveAll(Arrays.asList(cat1, cat2, cat3, cat4)));
    }

    private void createChallenges() {
        Region region = regions.get(1); // 강남구

        // 1. 종료된 챌린지
        Challenge finishedChallenge = new Challenge(region, "매일 30분 달리기 (종료)", "건강을 위해 매일 함께 달려요!", 10,
                "매일 달리기 인증샷 업로드", LocalDate.now().minusDays(30), LocalDate.now().minusDays(1),
                "https://picsum.photos/200/300", false, categories.get(0));
        challenges.add(challengeRepository.save(finishedChallenge));
        chatRoomRepository.save(new ChatRoom(finishedChallenge.getId().toString(), finishedChallenge.getTitle(),
                users.get(0).getId(), Arrays.asList(users.get(0).getId()), Instant.now()));


        // 2. 진행 중인 챌린지
        Challenge ongoingChallenge = new Challenge(region, "코딩테스트 매일 1문제 풀기 (진행중)", "알고리즘 마스터가 되기 위한 여정", 20,
                "매일 문제풀이 인증샷 업로드", LocalDate.now().minusDays(15), LocalDate.now().plusDays(15),
                "https://picsum.photos/200/300", true, categories.get(1));
        challenges.add(challengeRepository.save(ongoingChallenge));
        chatRoomRepository.save(new ChatRoom(ongoingChallenge.getId().toString(), ongoingChallenge.getTitle(),
                users.get(1).getId(), Arrays.asList(users.get(1).getId()), Instant.now()));


        // 3. 시작 전 챌린지
        Challenge upcomingChallenge = new Challenge(region, "미라클 모닝 오전 6시 기상 (시작전)", "아침 시간을 지배하는 자가 하루를 지배한다!", 15,
                "매일 기상시간 인증샷 업로드", LocalDate.now().plusDays(7), LocalDate.now().plusDays(37),
                "https://picsum.photos/200/300", true, categories.get(2));
        challenges.add(challengeRepository.save(upcomingChallenge));
        chatRoomRepository.save(new ChatRoom(upcomingChallenge.getId().toString(), upcomingChallenge.getTitle(),
                users.get(2).getId(), Arrays.asList(users.get(2).getId()), Instant.now()));
    }

    private void createChallengeApplicationsAndMembers() {
        Challenge ongoingChallenge = challenges.get(1); // 진행 중인 챌린지

        // 신청자 1: 성실한챌린저 (user1) - 승인
        User applicant1 = users.get(0);
        ChallengeApplication app1 = new ChallengeApplication("안녕하세요!", "열심히 참여하겠습니다.", "매일 인증!",
                ChallengeApplyStatus.APPROVED, "환영합니다!", applicant1, ongoingChallenge);
        challengeApplicationRepository.save(app1);
        challengeMemberRepository.save(new ChallengeMember("MEMBER", ongoingChallenge, applicant1));

        // 신청자 2: 가끔챌린저 (user3) - 승인
        User applicant2 = users.get(2);
        ChallengeApplication app2 = new ChallengeApplication("반갑습니다.", "함께하고 싶어요.", "노력하겠습니다.",
                ChallengeApplyStatus.APPROVED, "어서오세요!", applicant2, ongoingChallenge);
        challengeApplicationRepository.save(app2);
        challengeMemberRepository.save(new ChallengeMember("MEMBER", ongoingChallenge, applicant2));


        // 신청자 3: 구경꾼 (user4) - 대기
        User applicant3 = users.get(3);
        ChallengeApplication app3 = new ChallengeApplication("저도 참여하고 싶습니다.", "잘 부탁드립니다.", "꾸준히 해볼게요.",
                ChallengeApplyStatus.PENDING, null, applicant3, ongoingChallenge);
        challengeApplicationRepository.save(app3);
    }

    private void createChallengeVerifications() {
        Challenge ongoingChallenge = challenges.get(1); // 진행 중인 챌린지
        List<ChallengeMember> members = challengeMemberRepository.findByChallengeId(ongoingChallenge.getId());

        // 멤버 1 (성실한챌린저): 거의 매일 인증 성공 (APPROVED)
        ChallengeMember member1 = members.get(0);
        for (int i = 1; i < 15; i++) {
            if (random.nextInt(10) < 9) { // 90% 확률로 인증
                ChallengeVerification verification = new ChallengeVerification(
                        LocalDate.now().minusDays(i).atStartOfDay(), "https://picsum.photos/200/300", "오늘도 성공!",
                        ChallengeVerificationStatus.APPROVED, null, ongoingChallenge, member1.getUser());
                challengeVerificationRepository.save(verification);
            }
        }

        // 멤버 2 (가끔챌린저): 다양한 상태의 인증
        ChallengeMember member2 = members.get(1);
        for (int i = 1; i < 15; i++) {
            if (random.nextBoolean()) { // 50% 확률로 인증 시도
                int statusRoll = random.nextInt(10);
                ChallengeVerificationStatus status;
                String rejectionReason = null;
                if (statusRoll < 6) { // 60% 확률로 승인
                    status = ChallengeVerificationStatus.APPROVED;
                } else if (statusRoll < 9) { // 30% 확률로 거절
                    status = ChallengeVerificationStatus.REJECTED;
                    rejectionReason = "인증 사진이 명확하지 않습니다.";
                } else { // 10% 확률로 대기
                    status = ChallengeVerificationStatus.PENDING;
                }

                ChallengeVerification verification = new ChallengeVerification(
                        LocalDate.now().minusDays(i).atStartOfDay(), "https://picsum.photos/200/300", "인증합니다.",
                        status, rejectionReason, ongoingChallenge, member2.getUser());
                challengeVerificationRepository.save(verification);
            }
        }

        // 멤버 3 (열심챌린저 - 챌린지 개설자): 인증 안함
        // 별도 로직 없음
    }
} 