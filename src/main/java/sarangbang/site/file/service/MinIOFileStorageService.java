package sarangbang.site.file.service;

import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 📦 MinIO 파일 저장 서비스
 * 
 * 🎯 MinIO란?
 * - AWS S3와 100% 호환되는 오픈소스 객체 저장소
 * - Docker로 쉽게 설치 가능
 * - 개발/테스트 환경에서 S3 대신 사용
 * - 실제 운영에서는 S3로 쉽게 전환 가능
 */
@Slf4j
@Service
@Profile("!prod")  // 운영 환경이 아닐 때만 활성화
@RequiredArgsConstructor
public class MinIOFileStorageService implements FileStorageService {

    private final MinioClient minioClient;

    @Value("${minio.bucket-name}")
    private String bucketName;

    @Value("${minio.url}")
    private String minioUrl;

    /**
     * 🏗️ 서비스 초기화: 버킷 생성 및 확인
     */
    @PostConstruct
    public void initializeBucket() {
        try {
            log.info("🏗️ MinIO 버킷 초기화 시작: bucketName={}, url={}", bucketName, minioUrl);

            // 버킷이 존재하는지 확인
            boolean bucketExists = minioClient.bucketExists(
                BucketExistsArgs.builder()
                    .bucket(bucketName)
                    .build()
            );

            if (!bucketExists) {
                // 버킷이 없으면 생성
                minioClient.makeBucket(
                    MakeBucketArgs.builder()
                        .bucket(bucketName)
                        .build()
                );
                log.info("✅ MinIO 버킷 생성 완료: {}", bucketName);
            } else {
                log.info("✅ MinIO 버킷 확인 완료: {}", bucketName);
            }

        } catch (Exception e) {
            log.error("💥 MinIO 버킷 초기화 실패: bucketName={}", bucketName, e);
            throw new FileStorageException("MinIO 버킷 초기화 실패: " + e.getMessage(), e);
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

            log.debug("📤 MinIO 파일 업로드 시작: filePath={}, size={}bytes", filePath, file.getSize());

            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .stream(file.getInputStream(), file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build()
            );

            log.info("✅ MinIO 파일 업로드 성공: filePath={}, size={}bytes", filePath, file.getSize());

        } catch (Exception e) {
            log.error("❌ MinIO 파일 업로드 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 📥 파일 다운로드
     */
    @Override
    public byte[] downloadFile(String filePath) {
        try {
            log.debug("📥 MinIO 파일 다운로드 시작: filePath={}", filePath);

            InputStream inputStream = minioClient.getObject(
                GetObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[8192];
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            byte[] fileData = outputStream.toByteArray();
            log.debug("✅ MinIO 파일 다운로드 성공: filePath={}, size={}bytes", filePath, fileData.length);

            return fileData;

        } catch (Exception e) {
            log.error("❌ MinIO 파일 다운로드 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 다운로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 🗑️ 파일 삭제
     */
    @Override
    public void deleteFile(String filePath) {
        try {
            log.debug("🗑️ MinIO 파일 삭제 시작: filePath={}", filePath);

            minioClient.removeObject(
                RemoveObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );

            log.info("✅ MinIO 파일 삭제 성공: filePath={}", filePath);

        } catch (Exception e) {
            log.error("❌ MinIO 파일 삭제 실패: filePath={}", filePath, e);
            throw new FileStorageException("파일 삭제에 실패했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 🔍 파일 존재 확인
     */
    @Override
    public boolean fileExists(String filePath) {
        try {
            minioClient.statObject(
                StatObjectArgs.builder()
                    .bucket(bucketName)
                    .object(filePath)
                    .build()
            );
            
            log.debug("✅ MinIO 파일 존재 확인: filePath={} → 존재함", filePath);
            return true;

        } catch (Exception e) {
            log.debug("❌ MinIO 파일 존재 확인: filePath={} → 존재하지 않음", filePath);
            return false;
        }
    }
}
