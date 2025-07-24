package gift.product.dto;


import gift.product.entity.Item;
import gift.product.entity.WishList;


public record GetWishListResponse(
	Long id,
	Long itemId,
	String itemName,
	Integer price,
	String imageUrl,
	int amount
) {
	public static GetWishListResponse from(WishList wishList) {
		Item item = wishList.getItem();
		return new GetWishListResponse(
			wishList.getId(),
			item.getId(),
			item.getName(),
			item.getPrice(),
			item.getImageUrl(),
			wishList.getAmount()
		);
	}
}
