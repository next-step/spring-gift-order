package gift.common.exception;

public class InvalidAccessTokenException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Authorization 헤더가 유효하지 않습니다.";
    public InvalidAccessTokenException() {
        super(DEFAULT_MESSAGE);
    }
}
