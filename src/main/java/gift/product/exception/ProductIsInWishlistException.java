package gift.product.exception;

public class ProductIsInWishlistException extends RuntimeException {
    public ProductIsInWishlistException(String name) {
        super("Product is already in the wishlist: " + name);
    }
}
