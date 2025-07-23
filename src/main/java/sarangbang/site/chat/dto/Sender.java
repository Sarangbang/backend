package sarangbang.site.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sender {

    private String userId;
    private String nickname;
    private String profileImageUrl;
}
