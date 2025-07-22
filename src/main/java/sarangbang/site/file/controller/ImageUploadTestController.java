// package sarangbang.site.file.controller;
//
// import io.swagger.v3.oas.annotations.Operation;
// import io.swagger.v3.oas.annotations.tags.Tag;
// import lombok.RequiredArgsConstructor;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;
// import sarangbang.site.file.service.ImageUploadService;
//
// @Tag(name = "image-upload-test-controller", description = "🔧 이미지 업로드 테스트용 컨트롤러")
// @RestController
// @RequestMapping("/api/test/image")
// @RequiredArgsConstructor
// public class ImageUploadTestController {
//
//     private final ImageUploadService imageUploadService;
//
//     @Operation(summary = "프로필 이미지 업로드", description = "테스트용 유저 ID로 프로필 이미지 업로드")
//     @PostMapping(value = "/profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<String> uploadProfileImage(
//             @RequestPart("file") MultipartFile file
//     ) {
//         String imageUrl = imageUploadService.storeProfileImage(file, 123L);
//         return ResponseEntity.ok(imageUrl);
//     }
//
//     @Operation(summary = "챌린지 대표 이미지 업로드")
//     @PostMapping(value = "/challenge", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<String> uploadChallengeImage(
//             @RequestPart("file") MultipartFile file,
//             @RequestParam("challengeId") Long challengeId
//     ) {
//         String imageUrl = imageUploadService.storeChallengeImage(file, challengeId);
//         return ResponseEntity.ok(imageUrl);
//     }
//
//     @Operation(summary = "챌린지 인증 이미지 업로드")
//     @PostMapping(value = "/proof", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//     public ResponseEntity<String> uploadProofImage(
//             @RequestPart("file") MultipartFile file
//     ) {
//         String imageUrl = imageUploadService.storeProofImage(file, 789L);
//         return ResponseEntity.ok(imageUrl);
//     }
// }
