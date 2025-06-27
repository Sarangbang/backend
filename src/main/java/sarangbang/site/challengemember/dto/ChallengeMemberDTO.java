package sarangbang.site.challengemember.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChallengeMemberDTO {

    private Long id;
    private String userId;
    private Long challengeId;
    private String role;
}
