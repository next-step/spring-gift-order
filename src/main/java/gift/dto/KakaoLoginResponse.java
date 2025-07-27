package gift.dto;

public record KakaoLoginResponse (
        String accessToken,
        String tokenType,
        String refreshToken,
        int expiresIn,
        String scope
){
}
