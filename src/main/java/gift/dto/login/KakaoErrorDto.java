package gift.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoErrorDto(
    @JsonProperty(value = "code") int code,
    @JsonProperty(value = "msg") String message
) {

}
