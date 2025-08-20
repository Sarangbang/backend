package sarangbang.site.global.config;

import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class MinIOConfig {

    private final StorageProperties storageProperties;

    @Bean
    public MinioClient minioClient() {
        MinioClient minioClient =  MinioClient.builder()
                .endpoint(storageProperties.getEndpoint())
                .credentials(storageProperties.getAccessKey(), storageProperties.getSecretKey())
                .build();
        return minioClient;
    }

}
