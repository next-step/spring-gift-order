package gift.common.exception;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "해당 이메일의 유저가 이미 존재합니다.";

    public UserAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
