package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoMessageResponse(
        @JsonProperty("result_code")
        Integer resultCode,
        
        @JsonProperty("result_msg")
        String resultMsg
) {
}
