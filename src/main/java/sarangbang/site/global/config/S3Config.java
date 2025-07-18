package sarangbang.site.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("prod")  // 운영환경에서만 활성화
public class S3Config {

    @Value("${app.storage.region:ap-northeast-2}")
    private String region;

    // 임시 테스트용 (나중에 제거)
    @Value("${app.storage.access-key:}")
    private String accessKey;

    @Value("${app.storage.secret-key:}")
    private String secretKey;

    @Bean
    public S3Client s3Client() {
        if (!accessKey.isEmpty() && !secretKey.isEmpty()) {
            System.out.println("🔑 S3 Access Key 방식으로 연결 중... (로컬 테스트용)");
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(
                            StaticCredentialsProvider.create(
                                    AwsBasicCredentials.create(accessKey, secretKey)
                            )
                    )
                    .build();
        } else {
            System.out.println("🔐 S3 IAM Role 방식으로 연결 중... (운영용)");
            return S3Client.builder()
                    .region(Region.of(region))
                    .credentialsProvider(DefaultCredentialsProvider.create())
                    .build();
        }
    }
}
