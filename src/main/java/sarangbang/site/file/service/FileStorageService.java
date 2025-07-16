package sarangbang.site.file.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 📁 파일 저장소 서비스 인터페이스
 * 
 * 🎯 목적: 
 * - 다양한 저장소(Local, MinIO, S3)에 대한 통일된 접근 방법 제공
 * - 저장소 변경 시 최소한의 코드 수정으로 대응 가능
 * 
 * 📋 구현체들:
 * - MinIOFileStorageService: MinIO 저장 (개발/테스트)
 * - S3FileStorageService: AWS S3 저장 (운영)
 * 
 * 🔧 디자인 패턴: Strategy Pattern (전략 패턴)
 */
public interface FileStorageService {

    /**
     * 파일을 저장합니다
     * 
     * @param file 업로드할 파일
     * @param filePath 저장할 경로 (예: "profiles/20250714_abc123.jpg")
     * @throws RuntimeException 저장 실패 시 발생
     */
    void uploadFile(MultipartFile file, String filePath);

    /**
     * 파일을 다운로드합니다
     * 
     * @param filePath 다운로드할 파일 경로
     * @return 파일의 바이트 데이터
     * @throws RuntimeException 파일을 찾을 수 없거나 읽기 실패 시 발생
     */
    byte[] downloadFile(String filePath);

    /**
     * 파일을 삭제합니다
     * 
     * @param filePath 삭제할 파일 경로
     * @throws RuntimeException 삭제 실패 시 발생
     */
    void deleteFile(String filePath);

    /**
     * 파일이 존재하는지 확인합니다
     * 
     * @param filePath 확인할 파일 경로
     * @return 파일 존재 여부
     */
    boolean fileExists(String filePath);
}
