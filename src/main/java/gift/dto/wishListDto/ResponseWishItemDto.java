package gift.dto.wishListDto;

import gift.entity.WishItem;

public record ResponseWishItemDto(Long id, Long userId, Long itemId, Integer quantity) {

    public static ResponseWishItemDto from(WishItem wishItem) {
        return new ResponseWishItemDto(wishItem.getId(), wishItem.getUser().getId(), wishItem.getItem().getId(), wishItem.getQuantity());
    }
}
