package sarangbang.site.file.exception;

/**
 * 📁 파일 저장 관련 예외
 * 
 * 🎯 사용 상황:
 * - 파일 크기 초과
 * - 지원하지 않는 파일 형식
 * - 저장소 접근 실패
 * - 파일명 오류 등
 */
public class FileStorageException extends RuntimeException {

    public FileStorageException(String message) {
        super(message);
    }

    public FileStorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
