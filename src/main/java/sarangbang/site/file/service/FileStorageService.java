package sarangbang.site.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 파일 저장소 서비스 인터페이스
 *
 * 목적:
 * - 다양한 저장소(Local, MinIO, S3 등)에 대한 통일된 접근 방식 제공
 * - 저장소 변경 시 서비스 로직의 영향 최소화
 *
 * 📋 구현체들:
 * - MinIOFileStorageService: MinIO에 저장 (개발 환경)
 * - S3FileStorageService: AWS S3에 저장 (운영 환경)
 * - DelegatingFileStorageService: 설정에 따라 구현체 자동 선택
 *
 * 🧠 디자인 패턴:
 * - Strategy Pattern (전략 패턴)
 * - + Delegation (위임) 패턴을 통한 구현체 자동 전환
 */
public interface FileStorageService {

    /**
     * 파일을 업로드합니다.
     *
     * @param file 업로드할 파일
     * @param filePath 저장 경로 (예: "profiles/123/profile.jpg")
     * @throws RuntimeException 저장 실패 시
     */
    void uploadFile(MultipartFile file, String filePath);

    /**
     * 파일을 다운로드합니다.
     *
     * @param filePath 다운로드할 파일 경로
     * @return 파일의 바이트 데이터
     * @throws RuntimeException 파일이 없거나 읽기 실패 시
     */
    byte[] downloadFile(String filePath);

    /**
     * 파일을 삭제합니다.
     *
     * @param filePath 삭제할 파일 경로
     * @throws RuntimeException 삭제 실패 시
     */
    void deleteFile(String filePath);

    /**
     * 파일이 존재하는지 확인합니다.
     *
     * @param filePath 확인할 경로
     * @return 존재하면 true, 없으면 false
     */
    boolean fileExists(String filePath);
}
