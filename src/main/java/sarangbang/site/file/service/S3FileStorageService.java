package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.storage.type", havingValue = "s3")
@Profile("prod")
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${app.storage.bucket-name}")
    private String bucketName;

    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        try {
            s3Client.putObject(
                    PutObjectRequest.builder()
                            .bucket(bucketName)
                            .key(filePath)
                            .contentType(file.getContentType())
                            .build(),
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize())
            );
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
                            .bucket(bucketName)
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
                    .bucket(bucketName)
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
                    .bucket(bucketName)
                    .key(filePath)
                    .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            throw new FileStorageException("S3 파일 존재 확인 실패: " + filePath, e);
        }
    }
}
