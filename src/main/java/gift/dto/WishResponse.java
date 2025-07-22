package gift.dto;

import gift.entity.Wish;

public record WishResponse(
    Long wishId,
    Long productId,
    Integer quantity,
    String productName,
    Integer productPrice,
    String imageUrl
) {

    public static WishResponse from(Wish wish) {
        return new WishResponse(
            wish.getId(),
            wish.getProduct().getId(),
            wish.getQuantity(),
            wish.getProduct().getName(),
            wish.getProduct().getPrice(),
            wish.getProduct().getImageUrl()
        );
    }
}
