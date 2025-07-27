package gift.dto;

public record AuthorizationRequest(
    String email,
    String password
){}