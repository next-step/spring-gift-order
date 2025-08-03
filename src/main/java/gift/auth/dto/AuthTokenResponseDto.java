package gift.auth.dto;

public record AuthTokenResponseDto(
        String jwtToken,
        String kakaoAccessToken
) {
}
