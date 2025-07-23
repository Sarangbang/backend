package sarangbang.site.file.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import sarangbang.site.file.enums.ImageType;
import sarangbang.site.file.exception.FileStorageException;

@Service
@RequiredArgsConstructor
public class ImageSaveFactory {

    private final ImageUploadService imageUploadService;

    public <T> String getImageUploadService(MultipartFile file, ImageType type, T personId) throws FileStorageException {
        imageUploadService.validateImageFile(file);
        return switch (type) {
            case PROFILE -> imageUploadService.storeProfileImage(file, (String) personId);
            case CHALLENGE -> imageUploadService.storeChallengeImage(file, (Long) personId);
            case VERTIFICATION -> imageUploadService.storeProofImage(file, (Long) personId);
            default -> throw new IllegalArgumentException("지원하지 않는 이미지 타입입니다: " + type);
        };
    }
}
