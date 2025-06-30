package sarangbang.site.challengeverification.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChallengeVerificationDTO {
    private Long id;
    private Long challengeId;
    private String imgUrl;
    private String content;
    private String status;

}