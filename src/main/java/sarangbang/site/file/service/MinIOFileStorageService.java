package sarangbang.site.file.service;

import io.minio.*;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;
import sarangbang.site.global.config.StorageProperties;

import java.io.InputStream;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Profile({"dev", "prod"})
public class MinIOFileStorageService implements FileStorageService {

    private final MinioClient minioClient;
    private final StorageProperties storageProperties;

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(storageProperties.getBucket())
                    .build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs
                        .builder()
                        .bucket(storageProperties.getBucket())
                        .build()
                );
            }
        } catch(Exception e) {
            throw new FileStorageException("버킷 초기화 실패", e);
        }
    }

    @Override
    public String uploadFile(MultipartFile file, String filePath) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(storageProperties.getBucket())
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
            return filePath;
        } catch (Exception e) {
            throw new FileStorageException("업로드 실패", e);
        }
    }

    @Override
    public byte[] downloadFile(String filePath) {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(storageProperties.getBucket())
                .object(filePath)
                .build())) {

            return stream.readAllBytes();
        } catch (Exception e) {
            throw new FileStorageException("다운로드 실패", e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(storageProperties.getBucket())
                    .object(filePath)
                    .build());
        } catch (Exception e) {
            throw new FileStorageException("삭제 실패", e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            minioClient.statObject(StatObjectArgs.builder()
                    .bucket(storageProperties.getBucket())
                    .object(filePath)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public String generatePresignedUrl(String objectName, Duration timeToLive) throws FileStorageException {

        if (objectName != null && objectName.startsWith("/images")) {
            return objectName;
        }

        // MinIO 클라이언트 생성
        MinioClient minioClient = MinioClient.builder()
                .endpoint(storageProperties.getEndpoint()) // MinIO 주소
                .credentials(storageProperties.getAccessKey(), storageProperties.getSecretKey()) // 접속 키
                .build();
        try{
            String url = minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET) // 다운로드용 presigned URL
                            .bucket(storageProperties.getBucket()) // 버킷 이름
                            .object(objectName) // 오브젝트 이름
                            .expiry(30, TimeUnit.MINUTES) // 유효 시간
                            .build()
            );
            return url;
        }catch (Exception e){
            throw new FileStorageException("Presigned URL 생성 실패", e);
        }
    }
}
