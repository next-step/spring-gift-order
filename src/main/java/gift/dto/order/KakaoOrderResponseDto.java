package gift.dto.order;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoOrderResponseDto(
    @JsonProperty("result_code") int resultCode
) {

}
