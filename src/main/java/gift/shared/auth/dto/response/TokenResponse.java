package gift.shared.auth.dto.response;

public record TokenResponse(
        String Token
) {
    public static TokenResponse from(String token) {
        return new TokenResponse(token);
    }
}
