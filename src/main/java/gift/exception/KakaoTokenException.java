package gift.exception;

public class KakaoTokenException extends RuntimeException {

    private int status;

    public KakaoTokenException(String message, int status) {
        super(message);
        this.status = status;
    }

    public KakaoTokenException(String message) {
        this(message, 500);
    }

    public int getStatus(){
        return status;
    }

}
