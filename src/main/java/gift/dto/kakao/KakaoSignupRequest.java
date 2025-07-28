package gift.dto.kakao;

public record KakaoSignupRequest(
        String accessToken,
        String email,
        String password
) {}
