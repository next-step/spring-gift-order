package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenRefreshResponse (
        @JsonProperty("token_type") String tokenType,
        @JsonProperty("access_token") String accessToken,
        @JsonProperty("expires_in") Integer expiresIn
) {}
