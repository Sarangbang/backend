package sarangbang.site.file.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sarangbang.site.file.service.FileStorageService;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 📥 파일 다운로드 컨트롤러
 * 
 * 🎯 역할: 업로드된 파일을 브라우저에서 조회할 수 있도록 제공
 * 
 * 📋 제공하는 API:
 * - GET /api/files/{path}/** : 파일 다운로드/조회
 */
@Slf4j
@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileDownloadController {

    private final FileStorageService fileStorageService;

    /**
     * 📥 파일 다운로드/조회 API
     * 
     * 사용 예시:
     * - GET /api/files/profiles/20250714_abc123.jpg
     * - GET /api/files/challenges/20250714_def456.png
     * - GET /api/files/verifications/20250714_ghi789.jpg
     * 
     * 브라우저에서 <img src="/api/files/profiles/20250714_abc123.jpg"> 형태로 사용
     */
    @GetMapping("/**")
    public ResponseEntity<byte[]> downloadFile(HttpServletRequest request) {
        try {
            // URL에서 파일 경로 추출
            String filePath = extractFilePath(request);
            
            log.debug("📥 파일 다운로드 요청: filePath={}", filePath);

            // 파일 저장소에서 파일 데이터 가져오기
            byte[] fileData = fileStorageService.downloadFile(filePath);
            
            // HTTP 헤더 설정
            HttpHeaders headers = new HttpHeaders();
            
            // 파일 타입 추정 (확장자 기반)
            String contentType = determineContentType(filePath);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // 파일 크기 설정
            headers.setContentLength(fileData.length);
            
            // 브라우저에서 바로 표시 (다운로드 아님)
            headers.set("Content-Disposition", "inline");
            
            log.debug("✅ 파일 다운로드 성공: filePath={}, size={}bytes", filePath, fileData.length);
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(fileData);
            
        } catch (Exception e) {
            log.error("❌ 파일 다운로드 실패: uri={}", request.getRequestURI(), e);
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * HTTP 요청에서 파일 경로 추출
     * 
     * 예시:
     * - 요청 URL: /api/files/profiles/20250714_abc123.jpg
     * - 결과: profiles/20250714_abc123.jpg
     */
    private String extractFilePath(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        String basePath = "/api/files/";
        
        if (requestURI.startsWith(basePath)) {
            return requestURI.substring(basePath.length());
        }
        
        throw new IllegalArgumentException("잘못된 파일 경로: " + requestURI);
    }

    /**
     * 파일 확장자로 Content-Type 추정
     */
    private String determineContentType(String filePath) {
        String lowerPath = filePath.toLowerCase();
        
        if (lowerPath.endsWith(".jpg") || lowerPath.endsWith(".jpeg")) {
            return "image/jpeg";
        } else if (lowerPath.endsWith(".png")) {
            return "image/png";
        } else if (lowerPath.endsWith(".gif")) {
            return "image/gif";
        } else if (lowerPath.endsWith(".webp")) {
            return "image/webp";
        } else {
            return "application/octet-stream";
        }
    }
}
