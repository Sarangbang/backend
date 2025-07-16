package sarangbang.site.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

/**
 * 📦 AWS S3 설정 클래스
 * 
 * AWS S3는 Amazon의 클라우드 객체 저장소입니다.
 * 운영 환경에서 안정적이고 확장 가능한 파일 저장을 제공합니다.
 */
@Slf4j
@Configuration
@Profile("prod")  // 운영 환경에서만 활성화
public class S3Config {

    @Value("${aws.access-key}")
    private String accessKey;

    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * S3 클라이언트 Bean 등록
     * 
     * 이 Bean을 통해 AWS S3와 통신할 수 있습니다.
     * - 파일 업로드
     * - 파일 다운로드  
     * - 버킷 관리
     * - 권한 설정 등
     */
    @Bean
    public S3Client s3Client() {
        try {
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);

            S3Client client = S3Client.builder()
                    .region(Region.of(region))                                    // AWS 리전 설정
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))  // 인증 정보
                    .build();

            log.info("AWS S3 클라이언트 초기화 완료: region={}", region);
            
            return client;
            
        } catch (Exception e) {
            log.error("AWS S3 클라이언트 초기화 실패", e);
            throw new RuntimeException("S3 설정 실패", e);
        }
    }
}
