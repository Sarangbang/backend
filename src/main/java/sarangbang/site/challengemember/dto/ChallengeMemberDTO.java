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

    private int id;
    private int userId;
    private int challengeId;
    private String role;
}
