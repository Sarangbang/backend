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

    private long id;
    private long userId;
    private long challengeId;
    private String role;
}
