package gift.dto.order;

import java.time.Instant;

public record OrderResponse(
        Long id,
        Long optionId,
        Long productId,
        Integer quantity,
        Long totalPrice,
        String optionName,
        String productName,
        String ImageUrl,
        Instant createdAt,
        Instant updatedAt
) {

}
