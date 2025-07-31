package gift.exception;

public class KakaoServerException extends KakaoException {

    private final int code;

    public KakaoServerException(int code, String message) {
        super(message);
        this.code = code;
    }

    public KakaoServerException(String message) {
        this(-999, message);
    }

    public int getCode() {
        return code;
    }
}
