package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record KakaoLoginResponse (
        @JsonProperty("access_token")
        String accessToken,

        @JsonProperty("token_type")
        String tokenType,

        @JsonProperty("refresh_token")
        String refreshToken,

        @JsonProperty("expires_in")
        int expiresIn,

        String scope
){
}
