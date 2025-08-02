package gift.dto.external;

public record KakaoTokenRequest(
        String grantType,
        String clientId,
        String redirectUri,
        String code
) {
}
