package gift.kakao.exception;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(String message) {
        super(message);
    }

    public TokenExpiredException() {
        super("토큰이 만료되었습니다.");
    }
}
