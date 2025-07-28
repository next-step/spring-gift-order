package gift.dto.kakao;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record KakaoTokenResponse(
    String tokenType,
    String accessToken,
    int expiresIn,
    String refreshToken,
    int refreshTokenExpire
) {

}
