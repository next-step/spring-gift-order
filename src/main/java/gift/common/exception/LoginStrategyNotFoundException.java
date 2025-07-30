package gift.common.exception;

public class LoginStrategyNotFoundException extends RuntimeException {
    public LoginStrategyNotFoundException(String message) {
        super(message);
    }
}
