package gift.dto.login;

public record KakaoLoginRequest(Long kakaoId, String accessToken) implements LoginRequest {
}
