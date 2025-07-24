package gift.dto;

public record KakaoLoginResponse (
        String access_token,
        String token_type,
        String refresh_token,
        int expires_in,
        String scope
){
}
