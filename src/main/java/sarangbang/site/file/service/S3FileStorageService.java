package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.core.sync.ResponseTransformer;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import jakarta.annotation.PostConstruct;
import java.io.IOException;

/**
 * 📦 AWS S3 파일 저장 서비스
 * - 실제로 AWS S3에 파일을 저장하고 가져오는 작업
 * - 운영 환경에서만 활성화
 */
@Slf4j
@Service
@Profile("prod")  // 운영 환경에서만 활성화
@RequiredArgsConstructor
public class S3FileStorageService implements FileStorageService {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    /**
     * 🏗️ 서비스 초기화: 버킷 존재 확인
     */
    @PostConstruct
    public void initializeBucket() {
        try {
            log.info("🏗️ S3 버킷 초기화 시작: bucketName={}, region={}", bucketName, region);

            // 버킷이 존재하는지 확인
            HeadBucketRequest headBucketRequest = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            s3Client.headBucket(headBucketRequest);
            log.info("✅ S3 버킷 확인 완료: {}", bucketName);

        } catch (NoSuchBucketException e) {
            log.error("💥 S3 버킷이 존재하지 않습니다: bucketName={}", bucketName, e);
            throw new FileStorageException("S3 버킷이 존재하지 않습니다: " + bucketName, e);
        } catch (Exception e) {
            log.error("💥 S3 버킷 초기화 실패: bucketName={}", bucketName, e);
            throw new FileStorageException("S3 버킷 초기화 실패: " + e.getMessage(), e);
        }
    }

    /**
     * 📤 파일 업로드
     */
    @Override
    public void uploadFile(MultipartFile file, String filePath) {
        try {
            if (file.isEmpty()) {
                throw new FileStorageException("업로드할 파일이 비어있습니다.");
            }

            log.debug("📤 S3 파일 업로드 시작: filePath={}, size={}bytes", filePath, file.getSize());

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .contentType(file.getContentType())
                    .contentLength(file.getSize())
                    .build();

            s3Client.putObject(putObjectRequest, 
                    RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            log.info("✅ S3 파일 업로드 성공: filePath={}, size={}bytes", filePath, file.getSize());

        } catch (IOException e) {
            log.error("❌ S3 파일 업로드 실패 (IO): filePath={}", filePath, e);
            throw new FileStorageException("파일 읽기에 실패했습니다: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("❌ S3 파일 업로드 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 📥 파일 다운로드
     */
    @Override
    public byte[] downloadFile(String filePath) {
        try {
            log.debug("📥 S3 파일 다운로드 시작: filePath={}", filePath);

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            byte[] fileData = s3Client.getObject(getObjectRequest, 
                    ResponseTransformer.toBytes()).asByteArray();

            log.debug("✅ S3 파일 다운로드 성공: filePath={}, size={}bytes", filePath, fileData.length);

            return fileData;

        } catch (NoSuchKeyException e) {
            log.error("❌ S3 파일을 찾을 수 없습니다: filePath={}", filePath, e);
            throw new FileStorageException("파일을 찾을 수 없습니다: " + filePath, e);
        } catch (Exception e) {
            log.error("❌ S3 파일 다운로드 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 다운로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 🗑️ 파일 삭제
     */
    @Override
    public void deleteFile(String filePath) {
        try {
            log.debug("🗑️ S3 파일 삭제 시작: filePath={}", filePath);

            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("✅ S3 파일 삭제 성공: filePath={}", filePath);

        } catch (Exception e) {
            log.error("❌ S3 파일 삭제 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 🔍 파일 존재 확인
     */
    @Override
    public boolean fileExists(String filePath) {
        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(filePath)
                    .build();

            s3Client.headObject(headObjectRequest);
            
            log.debug("✅ S3 파일 존재 확인: filePath={} → 존재함", filePath);
            return true;

        } catch (NoSuchKeyException e) {
            log.debug("❌ S3 파일 존재 확인: filePath={} → 존재하지 않음", filePath);
            return false;
        } catch (Exception e) {
            log.error("❌ S3 파일 존재 확인 실패: filePath={}", filePath, e);
            return false;
        }
    }
}
