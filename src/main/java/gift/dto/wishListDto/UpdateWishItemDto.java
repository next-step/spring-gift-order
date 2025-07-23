package gift.dto.wishListDto;

import gift.validation.itemPolicy.ItemFieldValid;

@ItemFieldValid
public record UpdateWishItemDto(String itemName, String name, Integer quantity) {
}
