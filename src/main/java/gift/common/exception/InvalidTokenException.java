package gift.common.exception;

public class InvalidTokenException extends RuntimeException {
    public InvalidTokenException(Throwable cause) {
        super(cause);
    }

    public InvalidTokenException() {
    }
}
