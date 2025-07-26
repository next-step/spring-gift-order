package gift.exception;

public class KakaoTokenFetchException extends RuntimeException {
    private final int code;

    public KakaoTokenFetchException(int code, String message) {
        super(message);
        this.code = code;
    }

    public KakaoTokenFetchException(String message) {
        this(-999, message);
    }

    public int getCode() {
        return code;
    }
}
