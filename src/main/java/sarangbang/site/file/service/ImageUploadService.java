package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;                    // 🔧 Lombok: 생성자 자동 생성
import lombok.extern.slf4j.Slf4j;                        // 🔧 Lombok: 로깅 자동 설정
import org.springframework.beans.factory.annotation.Value; // 🔧 Spring: 설정값 주입
import org.springframework.stereotype.Service;            // 🔧 Spring: 서비스 계층 선언
import org.springframework.web.multipart.MultipartFile;  // 🔧 Spring: 파일 업로드 처리
import sarangbang.site.file.dto.ImageUploadResponseDTO;
import sarangbang.site.file.enums.ImageUsage;            // 📦 우리 프로젝트: 이미지 용도 열거형
import sarangbang.site.file.exception.FileStorageException; // 📦 우리 프로젝트: 파일 저장 예외

import java.time.LocalDateTime;                           // 🔧 Java: 날짜/시간 처리
import java.time.format.DateTimeFormatter;               // 🔧 Java: 날짜 형식 변환
import java.util.Arrays;                                  // 🔧 Java: 배열 유틸리티
import java.util.List;                                    // 🔧 Java: 리스트 컬렉션
import java.util.UUID;                                    // 🔧 Java: 고유 식별자 생성

/**
 * 📤 이미지 업로드 서비스
 *
 * 🎯 주요 기능:
 * 1. 이미지 파일 검증 (크기, 타입, 이름)
 * 2. 고유한 파일명 생성 (중복 방지)
 * 3. MinIO에 실제 파일 저장
 * 4. 접근 가능한 URL 생성
 * 5. 에러 처리 및 응답 생성
 */
@Slf4j                                                    // 🔧 Lombok: log 객체 자동 생성
@Service                                                  // 🔧 Spring: 서비스 빈 등록
@RequiredArgsConstructor                                  // 🔧 Lombok: final 필드 생성자 자동 생성
public class ImageUploadService {

    // 📦 우리 프로젝트: 실제 파일 저장을 담당하는 서비스 (인터페이스 사용)
    private final FileStorageService fileStorageService;

    // ⚙️ 설정값: 최대 파일 크기 (application.yml에서 읽어옴)
    @Value("${app.upload.max-file-size:5242880}")          // 🔧 Spring: 설정값 주입 (기본값: 5MB)
    private long maxFileSize;

    // ⚙️ 설정값: 허용할 이미지 타입들
    @Value("${app.upload.allowed-image-types:image/jpeg,image/jpg,image/png,image/gif,image/webp}")
    private String allowedImageTypesStr;                   // 🔧 Spring: 쉼표로 구분된 문자열

    /**
     * 🎯 메인 메서드: 이미지 업로드 처리
     *
     * 📋 처리 순서:
     * 1. 파일 기본 검증
     * 2. 고유한 파일명 생성
     * 3. MinIO에 파일 저장
     * 4. 접근 URL 생성
     * 5. 성공 응답 반환
     *
     * @param file 업로드할 이미지 파일
     * @param usage 이미지 사용 용도
     * @return 업로드 결과 응답
     */
    public ImageUploadResponseDTO uploadImage(MultipartFile file, ImageUsage usage) {
        try {
            log.info("🖼️ 이미지 업로드 시작: usage={}, filename={}, size={}bytes",
                    usage, file.getOriginalFilename(), file.getSize());

            // 1️⃣ 파일 기본 검증
            validateImageFile(file);

            // 2️⃣ 고유한 파일명 생성
            String uniqueFileName = generateUniqueFileName(file.getOriginalFilename());

            // 3️⃣ 저장 경로 생성 (용도별 폴더 구분)
            String storagePath = usage.getStoragePath();
            String fullPath = storagePath + uniqueFileName;

            // 4️⃣ MinIO에 실제 파일 저장
            fileStorageService.uploadFile(file, fullPath);

            // 5️⃣ 접근 가능한 URL 생성
            String imageUrl = generateImageUrl(fullPath);

            log.info("✅ 이미지 업로드 성공: usage={}, filename={}, imageUrl={}",
                    usage, file.getOriginalFilename(), imageUrl);

            // 6️⃣ 성공 응답 반환
            return ImageUploadResponseDTO.success(imageUrl);

        } catch (FileStorageException e) {
            // 🚫 파일 저장 관련 에러 (검증 실패, 저장 실패 등)
            log.error("❌ 이미지 업로드 실패 (FileStorageException): usage={}, filename={}",
                    usage, file.getOriginalFilename(), e);

            return ImageUploadResponseDTO.failure(
                    determineErrorCode(e.getMessage()),
                    e.getMessage()
            );

        } catch (Exception e) {
            // 🚫 예상치 못한 에러
            log.error("💥 이미지 업로드 실패 (UnexpectedException): usage={}, filename={}",
                    usage, file.getOriginalFilename(), e);

            return ImageUploadResponseDTO.failure(
                    "INTERNAL_SERVER_ERROR",
                    "이미지 업로드 중 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );
        }
    }

    /**
     * 🔍 이미지 파일 검증
     *
     * 📋 검증 항목:
     * 1. 빈 파일 체크
     * 2. 파일명 유효성 체크
     * 3. 파일 크기 체크
     * 4. 이미지 타입 체크
     *
     * @param file 검증할 파일
     * @throws FileStorageException 검증 실패 시 발생
     */
    private void validateImageFile(MultipartFile file) {
        // 🚫 빈 파일 체크
        if (file.isEmpty()) {
            throw new FileStorageException("업로드할 파일을 선택해주세요.");
        }

        // 🚫 파일명 체크
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new FileStorageException("파일명이 올바르지 않습니다.");
        }

        // 🚫 파일 크기 체크
        if (file.getSize() > maxFileSize) {
            String maxSizeMB = String.format("%.1f", maxFileSize / (1024.0 * 1024.0));
            String currentSizeMB = String.format("%.1f", file.getSize() / (1024.0 * 1024.0));

            throw new FileStorageException(
                    String.format("파일 크기가 너무 큽니다. (최대: %sMB, 현재: %sMB)", maxSizeMB, currentSizeMB)
            );
        }

        // 🚫 이미지 타입 체크
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList(allowedImageTypesStr.split(","));

        if (contentType == null || !allowedTypes.contains(contentType.toLowerCase())) {
            throw new FileStorageException(
                    String.format("지원하지 않는 이미지 형식입니다. (현재: %s, 지원형식: %s)",
                            contentType, String.join(", ", allowedTypes))
            );
        }

        log.debug("✅ 이미지 파일 검증 통과: filename={}, size={}bytes, type={}",
                originalFilename, file.getSize(), contentType);
    }

    /**
     * 🔄 중복되지 않는 고유한 파일명 생성
     *
     * 🎯 생성 규칙: "yyyyMMddHHmmss_UUID앞8자리_확장자"
     * 📋 예시: "20250714143020_a1b2c3d4.jpg"
     *
     * 장점:
     * - 시간 기반 정렬 가능
     * - UUID로 중복 방지
     * - 확장자 보존
     *
     * @param originalFilename 원본 파일명
     * @return 고유한 파일명
     */
    private String generateUniqueFileName(String originalFilename) {
        // 📅 현재 시간 (yyyyMMddHHmmss 형식)
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));

        // 🎲 UUID 앞 8자리 (중복 방지)
        String uuid = UUID.randomUUID().toString().substring(0, 8);

        // 📎 파일 확장자 추출
        String extension = "";
        if (originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        // 🏷️ 최종 파일명 조합
        String uniqueFileName = timestamp + "_" + uuid + extension;

        log.debug("🔄 고유 파일명 생성: {} → {}", originalFilename, uniqueFileName);

        return uniqueFileName;
    }

    /**
     * 🔗 이미지 접근 URL 생성
     *
     * @param fullPath 전체 파일 경로 (예: "profiles/20250714_abc123.jpg")
     * @return 접근 가능한 URL (예: "/api/files/profiles/20250714_abc123.jpg")
     */
    private String generateImageUrl(String fullPath) {
        return "/api/files/" + fullPath;
    }

    /**
     * 🚨 에러 메시지를 기반으로 에러 코드 결정
     *
     * 🎯 목적: 프론트엔드에서 에러 종류별 분기 처리할 수 있도록 함
     *
     * @param errorMessage 에러 메시지
     * @return 에러 코드
     */
    private String determineErrorCode(String errorMessage) {
        if (errorMessage.contains("파일을 선택해주세요")) {
            return "EMPTY_FILE";
        }
        if (errorMessage.contains("파일명이 올바르지 않습니다")) {
            return "INVALID_FILENAME";
        }
        if (errorMessage.contains("파일 크기가 너무 큽니다")) {
            return "FILE_TOO_LARGE";
        }
        if (errorMessage.contains("지원하지 않는 이미지 형식")) {
            return "INVALID_FILE_TYPE";
        }
        if (errorMessage.contains("MinIO") || errorMessage.contains("저장")) {
            return "STORAGE_ERROR";
        }

        return "UNKNOWN_ERROR";
    }
}