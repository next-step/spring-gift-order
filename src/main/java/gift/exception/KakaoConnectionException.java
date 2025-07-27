package gift.exception;

public class KakaoConnectionException extends RuntimeException {
    public KakaoConnectionException(String message) {
        super(message);
    }

    public KakaoConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
