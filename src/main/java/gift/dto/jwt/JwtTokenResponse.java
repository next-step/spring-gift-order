package gift.dto.jwt;

public record JwtTokenResponse(String accessToken) {
    public static JwtTokenResponse from(String accessToken) {
        return new JwtTokenResponse(accessToken);
    }
}
