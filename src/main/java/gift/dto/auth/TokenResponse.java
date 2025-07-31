package gift.dto.auth;

public record TokenResponse(
    String token
) {

    public static TokenResponse from(String token) {
        return new TokenResponse(
            token
        );
    }
}
