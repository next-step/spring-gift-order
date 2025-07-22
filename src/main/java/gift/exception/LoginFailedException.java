package gift.exception;

public class LoginFailedException extends RuntimeException {
    public LoginFailedException(String message) {
        super(message);
    }
    public LoginFailedException() {
        super("로그인 실패");
    }
}
