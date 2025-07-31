package gift.dto.kakao.template;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CommerceContentDto(
        String title,
        String imageUrl,
        LinkDto link
) {

}