package sarangbang.site.file.service;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;

import java.io.InputStream;

@Service
@RequiredArgsConstructor
@Profile("dev")
public class MinIOFileStorageService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${app.storage.bucket-name}")
    private String bucketName;

    @PostConstruct
    public void initializeBucket() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs
                    .builder()
                    .bucket(bucketName)
                    .build()
            );
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs
                        .builder()
                        .bucket(bucketName)
                        .build()
                );
            }
        } catch(Exception e) {
            throw new FileStorageException("버킷 초기화 실패", e);
        }
    }

    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        try {
            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());
        } catch (Exception e) {
            throw new FileStorageException("업로드 실패", e);
        }
    }

    @Override
    public byte[] downloadFile(String filePath) {
        try (InputStream stream = minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
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
                    .bucket(bucketName)
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
                    .bucket(bucketName)
                    .object(filePath)
                    .build());
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
