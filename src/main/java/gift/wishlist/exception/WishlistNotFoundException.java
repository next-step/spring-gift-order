package gift.wishlist.exception;

public class WishlistNotFoundException extends RuntimeException {

    public WishlistNotFoundException(Long wishlistId) {
        super("Wishlist not found with id: " + wishlistId);
    }
}
