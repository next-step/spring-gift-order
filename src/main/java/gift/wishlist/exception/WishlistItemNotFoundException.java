package gift.wishlist.exception;

public class WishlistItemNotFoundException extends RuntimeException {
    public WishlistItemNotFoundException(Long id) {
        super("Item not found with id: " + id);
    }
}
