package sarangbang.site.global.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "storage")
public class StorageProperties {
    private String type;
    private String endpoint;
    private String region;
    private String accessKey;
    private String secretKey;
    private String bucket;
    private String publicUrl;
}

