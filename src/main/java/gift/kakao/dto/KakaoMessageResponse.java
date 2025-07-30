package gift.kakao.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoMessageResponse(
    @JsonProperty("result_code")
    Integer resultCode
) {

}
