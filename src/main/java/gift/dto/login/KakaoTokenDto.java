package gift.dto.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoTokenDto(
    @JsonProperty String tokenType,
    @JsonProperty String accessToken,
    @JsonProperty long expiresIn,
    @JsonProperty String refreshToken,
    @JsonProperty long refreshTokenExpiresIn
) {

}
