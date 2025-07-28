package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KaKaoTokenInfo(
    @JsonProperty("access_token") String accessToken,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("expires_in") int expiresIn,
    @JsonProperty("refresh_token_expires_in") int refreshTokenExpiresIn

) {

}
