package gift.shared.auth.dto.request;

public record KakaoTokenRequest(
        String grant_type,
        String client_id,
        String redirect_uri,
        String code,
        String client_secret
) {
}
