package gift.event;

public record OrderPlacedEvent(
    String accessToken,
    String message,
    String imageUrl
) {}
