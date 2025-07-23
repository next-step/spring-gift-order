package gift.exception.wish;

public class WishAlreadyExistsException extends RuntimeException {
    public WishAlreadyExistsException(String message) {
        super(message);
    }
}
