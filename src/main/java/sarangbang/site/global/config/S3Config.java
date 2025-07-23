package sarangbang.site.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
@Profile("prod")  // 운영환경에서만 활성화
public class S3Config {

    @Value("${storage.region:ap-northeast-2}")
    private String region;

    @Bean
    public S3Client s3Client() {
        //NOTE: AWS IAM 역할로 부여합니다
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }
}
