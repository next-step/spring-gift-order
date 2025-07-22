package gift.shared.exception.option;

public class OverQuantityException extends RuntimeException {
    public OverQuantityException(String message) {
        super(message);
    }
}
