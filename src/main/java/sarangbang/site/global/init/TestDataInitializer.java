package sarangbang.site.global.init;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.security.crypto.password.PasswordEncoder;
import sarangbang.site.challenge.entity.Challenge;
import sarangbang.site.challenge.repository.ChallengeRepository;
import sarangbang.site.challengecategory.entity.ChallengeCategory;
import sarangbang.site.challengecategory.repository.ChallengeCategoryRepository;
import sarangbang.site.region.entity.Region;
import sarangbang.site.region.repository.RegionRepository;
import sarangbang.site.user.entity.User;
import sarangbang.site.user.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

@Profile("dev")
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(
        name = "spring.jpa.hibernate.ddl-auto", // 2-1. 검사할 속성의 이름을 지정합니다.
        havingValue = "create" // 2-2. 이 속성이 "create"라는 값을 가질 때만 이 컴포넌트를 활성화합니다.
)
public class TestDataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ChallengeRepository challengeRepository;
    private final ChallengeCategoryRepository challengeCategoryRepository;
    private final RegionRepository regionRepository;

    @Override
    public void run(String... args){
        userRepository.findByEmail("testuser@example.com").ifPresentOrElse(
                user -> System.out.println("✅ 테스트 유저 이미 존재"),
                () -> {
                    Region testRegion = regionRepository.findById(9L)
                            .orElseThrow(() -> new RuntimeException("테스트용 지역 데이터(ID: 9)가 없습니다."));
                    User user = new User(
                            "TEST-UUID",
                            "testuser@example.com",
                            passwordEncoder.encode("12345678"),
                            "TEST-NICKNAME",
                            "MALE",
                            testRegion,
                            null
                    );
                    userRepository.save(user);
                    System.out.println("✅ 테스트 유저 삽입 완료");
                }
        );

        // --- 2. 챌린지 상세조회용 테스트 데이터 생성 ---
        // "JPA 정복 스터디"라는 제목의 챌린지가 없는 경우에만 데이터를 생성합니다.
        if (challengeRepository.findByTitle("JPA 정복 스터디").isEmpty()) {
            // 카테고리가 존재하는지 먼저 확인합니다. ChallengeCategoryService에서 이미 생성해주지만, 안전을 위해 확인합니다.
            List<ChallengeCategory> categories = challengeCategoryRepository.findAll();
            if (categories.isEmpty()) {
                System.out.println("❌ 테스트 챌린지를 생성하기 위한 카테고리가 없습니다. ChallengeCategoryService를 확인해주세요.");
                return; // 카테고리가 없으면 중단합니다.
            }
            // "학습/도서" 카테고리를 찾습니다. 없으면 첫 번째 카테고리를 사용합니다.
            ChallengeCategory studyCategory = categories.stream()
                    .filter(c -> "학습/도서".equals(c.getCategoryName()))
                    .findFirst()
                    .orElse(categories.get(0)); // 없으면 그냥 첫번째꺼 사용

            Region challengeRegion = regionRepository.findById(9L)
                    .orElseThrow(() -> new RuntimeException("테스트용 지역 데이터(ID: 9)가 없습니다."));

            // 상세조회용 Challenge 객체를 생성합니다. 모든 필드를 채워줍니다.
            Challenge detailTestChallenge = new Challenge(
                    challengeRegion, // location
                    "JPA 정복 스터디", // title
                    "김영한님의 '자바 ORM 표준 JPA 프로그래밍' 책을 함께 완독하는 스터디입니다. 매주 정해진 분량을 읽고, 학습 내용을 공유하고, 토론합니다.", // description
                    10, // participants (최대 참여 인원)
                    "매주 월요일 자정까지 해당 주차의 학습 내용을 정리한 블로그 글 링크를 슬랙에 인증합니다.", // method
                    LocalDate.now().plusDays(1), // startDate (내일부터 시작)
                    LocalDate.now().plusDays(30), // endDate (30일 뒤 종료)
                    "https://cdn.inflearn.com/public/courses/324107/cover/5cf4d546-d749-43be-a713-de1361b98b9f/324107-eng.jpg", // image (예시 이미지 URL)
                    true, // status (활성화 상태)
                    studyCategory // challengeCategory (연결할 카테고리)
            );

            // 생성한 챌린지를 데이터베이스에 저장합니다.
            challengeRepository.save(detailTestChallenge);
            System.out.println("✅ 상세조회 테스트용 챌린지 데이터 삽입 완료");

        } else {
            System.out.println("✅ 상세조회 테스트용 챌린지 이미 존재");
        }
    }
}