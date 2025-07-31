package gift.dto;

public record KakaoTokenRequest(
    String grantType,
    String clientId,
    String redirectUri,
    String code,
    String clientSecret
) {

}
