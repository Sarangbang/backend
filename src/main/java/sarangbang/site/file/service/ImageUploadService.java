package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.exception.FileStorageException;
import java.util.Arrays;
import java.util.List;

// 이미지 업로드 서비스
@Slf4j
@Service
@RequiredArgsConstructor
public class ImageUploadService {

    private final FileStorageService fileStorageService; // 실제 파일 저장 담당 인터페이스

    @Value("${app.upload.max-file-size:5242880}")
    private long maxFileSize;

    @Value("${app.upload.allowed-image-types:image/jpeg,image/jpg,image/png,image/gif,image/webp}")
    private String allowedImageTypesStr;

    // 회원 프로필 이미지 저장
    public String storeProfileImage(MultipartFile file, Long userId) {
        try {
            log.info("프로필 이미지 업로드 시작: userId={}, filename={}, size={}bytes",
                    userId, file.getOriginalFilename(), file.getSize());

            // 1. 유효성 검증
            validateImageFile(file);

            // 2. 확장자 추출
            String extension = getExtension(file.getOriginalFilename());

            // 3. 저장 경로 설정 (ex. profiles/123/profile.jpg)
            String filePath = String.format("profiles/%d/profile%s", userId, extension);

            // 4. 파일 저장
            fileStorageService.uploadFile(file, filePath);

            // 5. 접근 가능한 URL 생성
            String imageUrl = "/api/files/" + filePath;

            log.info("프로필 이미지 업로드 완료: userId={}, imageUrl={}", userId, imageUrl);

            return imageUrl;

        } catch (Exception e) {
            log.error("프로필 이미지 업로드 실패: userId={}", userId, e);
            throw new FileStorageException("프로필 이미지 업로드에 실패했습니다: " + e.getMessage(), e);
        }
    }

    // 파일 유효성 검증
    private void validateImageFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new FileStorageException("업로드할 파일을 선택해주세요.");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileStorageException("파일명이 올바르지 않습니다.");
        }

        if (file.getSize() > maxFileSize) {
            throw new FileStorageException(String.format(
                    "파일 크기가 너무 큽니다. (최대: %.1fMB, 현재: %.1fMB)",
                    maxFileSize / (1024.0 * 1024.0),
                    file.getSize() / (1024.0 * 1024.0)
            ));
        }

        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList(allowedImageTypesStr.split(","));
        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new FileStorageException(String.format(
                    "지원하지 않는 이미지 형식입니다. (현재: %s)", contentType
            ));
        }
    }

    // 확장자 추출
    private String getExtension(String originalFilename) {
        if (originalFilename == null || !originalFilename.contains(".")) {
            return "";
        }
        return originalFilename.substring(originalFilename.lastIndexOf("."));
    }
}