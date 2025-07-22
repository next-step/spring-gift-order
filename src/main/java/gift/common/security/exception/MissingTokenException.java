package gift.common.security.exception;

public class MissingTokenException extends RuntimeException {

    public MissingTokenException() {
        super("인증 토큰이 필요합니다.");
    }
}
