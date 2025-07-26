package gift.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenDto(
    @JsonProperty(value = "token_type") String tokenType,
    @JsonProperty(value = "access_token") String accessToken,
    @JsonProperty(value = "expires_in") long expiresIn,
    @JsonProperty(value = "refresh_token") String refreshToken,
    @JsonProperty(value = "refresh_token_expires_in") long refreshTokenExpiresIn
) {

}
