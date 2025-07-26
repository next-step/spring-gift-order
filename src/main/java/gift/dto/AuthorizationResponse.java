package gift.dto;

public record AuthorizationResponse(
    String token
){

    public static AuthorizationResponse of(String token) {
        return new AuthorizationResponse(token);
    }
}
