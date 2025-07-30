package gift.dto.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoSendMessageResultCode(@JsonProperty("result_code") Integer resultCode) {
}
