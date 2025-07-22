package gift.wishlist.dto.response;

import gift.product.dto.response.ProductResponse;
import gift.wishlist.entity.Wishlist;

public record WishlistResponse(
    Long wishlistId,
    ProductResponse productResponse
) {
    public static WishlistResponse from(Wishlist wishlist){
        return new WishlistResponse(
                wishlist.getId(),
                ProductResponse.from(wishlist.getProduct())
        );
    }
}
