package gift.exception;

public class KakaoException extends RuntimeException {
    private final int code;

    public KakaoException(int code, String message) {
        super(message);
        this.code = code;
    }

    public KakaoException(String message) {
        this(-999, message);
    }

    public int getCode() {
        return code;
    }
}