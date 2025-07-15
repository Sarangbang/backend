package sarangbang.site.challengemember.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChallengeMemberDTO {

    @Schema(description = "챌린지 멤버 id", example = "1")
    private Long id;
    @Schema(description = "사용자 닉네임", example = "TEST-NICKNAME")
    private String nickname;
    @Schema(description = "사용자 role", example = "owner")
    private String role;
    @Schema(description = "챌린지 제목", example = "6시 기상 챌린지")
    private String challengeTitle;
    @Schema(description = "챌린지 인증 방법", example = "6시 전, 침대에서 일어나 사진을 찍어 올려주세요.")
    private String challengeMethod;
    @Schema(description = "인증 여부", example = "true")
    private boolean status;
}
