package gift.exception;

public class TokenUnauthorizedException extends RuntimeException {
    public TokenUnauthorizedException(String message) {
        super(message);
    }
    public TokenUnauthorizedException() { super("토큰 에러"); }
}
