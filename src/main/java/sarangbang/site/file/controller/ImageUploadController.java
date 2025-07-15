package sarangbang.site.file.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.dto.ImageUploadResponseDTO;
import sarangbang.site.file.enums.ImageUsage;
import sarangbang.site.file.service.ImageUploadService;

@Tag(name = "Image", description = "이미지 관련 API")
@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
public class ImageUploadController {

    // 실제 이미지 업로드 처리 담당 서비스
    private final ImageUploadService imageUploadService;

    /**
     *  이미지 업로드
     */
    @Operation(summary = "이미지 업로드", description = "이미지를 업로드합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadResponseDTO.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 크기, 형식)",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadResponseDTO.class))),
            @ApiResponse(responseCode = "401", description = "인증 실패",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "서버 오류",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ImageUploadResponseDTO.class)))
    })
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImageUploadResponseDTO> uploadImage(
            @RequestParam("file") MultipartFile file,    // 업로드할 이미지 파일
            @RequestParam("usage") ImageUsage usage
    ) {
        log.info("이미지 업로드 API 호출: usage={}, filename={}, size={}",
                usage, file.getOriginalFilename(), file.getSize());

        try {
            // 이미지 업로드 서비스 호출
            ImageUploadResponseDTO response = imageUploadService.uploadImage(file, usage);

            // 성공/실패에 따른 HTTP 상태코드 반환
            if (response.isSuccess()) {
                log.info("이미지 업로드 성공: usage={}, imageUrl={}", usage, response.getImageUrl());
                return ResponseEntity.ok(response); // 200 ok
            } else {
                log.warn("이미지 업로드 실패: usage={}, errorCode={}, message={}",
                        usage, response.getErrorCode(), response.getMessage());

                // 에러 코드에 따른 HTTP 상태코드 결정
                HttpStatus status = determineHttpStatus(response.getErrorCode());
                return ResponseEntity.status(status).body(response);
            }
        } catch (Exception e) {
            log.error("이미지 업로드 중 예상치 못한 에러: usage={}, filename={}",
                    usage, file.getOriginalFilename(), e);

            // 서버 에러 응답 생성
            ImageUploadResponseDTO errorResponse = ImageUploadResponseDTO.failure(
                    "INTERNAL_SERVER_ERROR",
                    "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
            );

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * 에러 코드에 따른 HTTP 상태코드 결정
     *
     * 목적: 클라이언트가 에러 종류를 HTTP 상태코드로 빠르게 파악할 수 있도록 함
     *
     * 규칙:
     * - 400 Bad Request: 클라이언트 실수 (파일 크기, 형식 등)
     * - 401 Unauthorized: 인증 문제
     * - 507 Insufficient Storage: 서버 저장 공간 문제
     * - 500 Internal Server Error: 기타 서버 문제
     */
    private HttpStatus determineHttpStatus(String errorCode) {
        if (errorCode == null) {
            return HttpStatus.INTERNAL_SERVER_ERROR; // 500 상태코드
        }

        return switch (errorCode) {
            case "FILE_TOO_LARGE", "INVALID_FILE_TYPE", "EMPTY_FILE", "INVALID_FILENAME"
                    -> HttpStatus.BAD_REQUEST;            // 🔧 Spring: 400 상태코드
            case "UNAUTHORIZED", "INVALID_TOKEN"
                    -> HttpStatus.UNAUTHORIZED;           // 🔧 Spring: 401 상태코드
            case "STORAGE_FULL", "QUOTA_EXCEEDED", "STORAGE_ERROR"
                    -> HttpStatus.INSUFFICIENT_STORAGE;   // 🔧 Spring: 507 상태코드
            default -> HttpStatus.INTERNAL_SERVER_ERROR;  // 🔧 Spring: 500 상태코드
        };
    }
}
