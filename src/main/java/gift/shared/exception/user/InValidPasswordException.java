package gift.shared.exception.user;

public class InValidPasswordException extends RuntimeException {
    public InValidPasswordException(String message) {
        super(message);
    }
}
