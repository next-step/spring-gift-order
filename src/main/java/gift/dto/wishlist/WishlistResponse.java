package gift.dto.wishlist;

import gift.domain.product.Product;
import gift.domain.Wishlist;

public class WishlistResponse {
    Long id;

    Long productId;
    String productName;
    String productImageUrl;

    public WishlistResponse(Wishlist wishlist) {
        Product product = wishlist.getProduct();
        this.id = wishlist.getId();
        this.productId = product.getId();
        this.productName = product.getName();
        this.productImageUrl = product.getImageUrl();
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductImageUrl() {
        return productImageUrl;
    }
}
