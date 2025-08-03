package gift.wish.dto;

import gift.product.dto.ProductResponseDto;

public record WishResponse(
    long id,
    Long optionId,
    ProductResponseDto product,
    String optionName,
    int quantity
) {
}
