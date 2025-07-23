package gift.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoProfileDto(
    @JsonProperty long id
) {

}
