package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoAccessTokenDTO(
    @JsonProperty("id")
    Long id,

    @JsonProperty("expires_in")
    Integer expiresIn,

    @JsonProperty("app_id")
    Integer appId
) {}

