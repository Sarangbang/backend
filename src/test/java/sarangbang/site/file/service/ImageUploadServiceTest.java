package sarangbang.site.file.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import java.awt.*;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class ImageUploadServiceTest {

    @Autowired
    private ImageUploadService imageUploadService;

    @Test
    public void testProfileImageUpload() throws Exception {
        // 1. 테스트 이미지 경로
        String path = "src/test/resources/test-profile.jpg";

        // 2. 파일 읽어서 MockMultipartFile 생성
        MockMultipartFile mockFile = new MockMultipartFile(
                "file",
                "test-profile.jpg",
                "image/jpeg",
                Files.readAllBytes(Paths.get(path))
        );

        // 3. 이미지 업로드 테스트 실행
        String imageUrl = imageUploadService.storeProfileImage(mockFile, "1L");

        // 4. 검증
        System.out.println("업로드 결과 URL: " + imageUrl);
        assert imageUrl != null;
    }
}
