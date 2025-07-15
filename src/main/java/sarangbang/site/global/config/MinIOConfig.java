package sarangbang.site.global.config;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * MinIO 설정 클래스
 * 
 * MinIO는 AWS S3와 호환되는 오픈소스 객체 저장소입니다.
 * 개발 환경에서 S3를 대신해서 사용할 수 있어 비용을 절약하면서도
 * 실제 S3와 동일한 API로 개발할 수 있습니다.
 */
@Slf4j
@Configuration
@Profile("!prod")  // 운영 환경이 아닐 때만 활성화 (개발, 로컬에서만 사용)
public class MinIOConfig {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    /**
     * MinIO 클라이언트 Bean 등록
     * 
     * 이 Bean을 통해 MinIO 서버와 통신할 수 있습니다.
     * - 파일 업로드
     * - 파일 다운로드  
     * - 버킷 관리
     * - 권한 설정 등
     */
    @Bean
    public MinioClient minioClient() {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(minioUrl)           // MinIO 서버 주소
                    .credentials(accessKey, secretKey)  // 인증 정보
                    .build();

            log.info("MinIO 클라이언트 초기화 완료: {}", minioUrl);
            
            return client;
            
        } catch (Exception e) {
            log.error("MinIO 클라이언트 초기화 실패", e);
            throw new RuntimeException("MinIO 설정 실패", e);
        }
    }
}
