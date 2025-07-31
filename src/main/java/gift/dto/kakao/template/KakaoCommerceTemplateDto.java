package gift.dto.kakao.template;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.List;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoCommerceTemplateDto(
        String objectType,
        CommerceContentDto content,
        CommerceDetailsDto commerce,
        List<ButtonDto> buttons
) {

}