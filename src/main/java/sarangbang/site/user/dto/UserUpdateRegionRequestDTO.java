package sarangbang.site.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class UserUpdateRegionRequestDTO {

    @NotNull(message = "변경할 지역 ID 를 입력해주세요.")
    @Schema(description = "지역 ID", example = "182")
    private Long regionId;
}
