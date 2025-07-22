package gift.wishlist.dto;

import java.time.LocalDateTime;

public record WishlistResponseDto(
    Long id,
    Long memberId,
    Long itemId,
    String itemName,
    Integer itemPrice,
    String itemImageUrl,
    LocalDateTime createdAt
) {

}