package gift.dto;

public record KakaoLoginResponse(
    String token_type,
    String access_token,
    String id_toekn,
    int expires_in,
    String refresh_token,
    int refresh_token_expires_in,
    String scope
) {

}
