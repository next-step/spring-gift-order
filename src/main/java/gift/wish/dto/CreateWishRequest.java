package gift.wish.dto;

public record CreateWishRequest(
    Long optionId,
    int quantity
) {
}
