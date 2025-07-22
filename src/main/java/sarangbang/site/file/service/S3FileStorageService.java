package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;
import sarangbang.site.global.config.StorageProperties;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final StorageProperties storageProperties;

    @Override
    public String uploadFile(MultipartFile file, String filePath) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(storageProperties.getBucket())
                            .key(filePath)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
            return filePath; // object key를 그대로 리턴
        } catch (IOException e) {
            throw new FileStorageException("S3 업로드 실패: " + filePath, e);
        }
    }

    @Override
    public byte[] downloadFile(String filePath) {
        try {
            GetObjectResponse response;
            byte[] data = s3Client.getObject(
                    GetObjectRequest.builder()
                            .bucket(storageProperties.getBucket())
                            .key(filePath)
                            .build()
            ).readAllBytes();

            return data;
        } catch (Exception e) {
            throw new FileStorageException("S3 다운로드 실패: " + filePath, e);
        }
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(storageProperties.getBucket())
                    .key(filePath)
                    .build());
        } catch (Exception e) {
            throw new FileStorageException("S3 삭제 실패: " + filePath, e);
        }
    }

    @Override
    public boolean fileExists(String filePath) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                    .bucket(storageProperties.getBucket())
                    .key(filePath)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new FileStorageException("S3 파일 존재 확인 실패: " + filePath, e);
        }
    }

    public String generatePresignedUrl(String objectKey, Duration expiration) throws FileStorageException {
        try {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(storageProperties.getBucket())
                    .key(objectKey)
                    .build();

            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(Duration.ofMinutes(10))
                    .getObjectRequest(getObjectRequest)
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);

            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("Presigned URL 생성 실패: {}", e.getMessage(), e);
            throw new FileStorageException("Presigned URL 생성에 실패했습니다.");
        }
    }

}
