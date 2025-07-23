package gift.common.exception;

public class InvalidUserException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "입력한 정보가 올바르지 않습니다.";
    public InvalidUserException() {
        super(DEFAULT_MESSAGE);
    }

    public InvalidUserException(String message) {
        super(message);
    }
}
