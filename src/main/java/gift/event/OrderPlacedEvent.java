package gift.event;

public record OrderPlacedEvent(
    Long wishId,
    String accessToken,
    String message,
    String imageUrl
) {}
