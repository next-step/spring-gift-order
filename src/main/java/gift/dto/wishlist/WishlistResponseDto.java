package gift.dto.wishlist;

import gift.entity.Product;

public record WishlistResponseDto(
        Long wishlistId,
        Long productId,
        String name,
        Long price,
        String imageUrl
) {
    public WishlistResponseDto(Long wishlistId, Product product) {
        this(wishlistId, product.getId(), product.getName(), product.getPrice(), product.getImageUrl());
    }
}