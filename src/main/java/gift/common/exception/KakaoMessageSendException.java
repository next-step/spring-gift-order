package gift.common.exception;

public class KakaoMessageSendException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "카카오 메세지 전송 중 오류가 발생하였습니다.";
    public KakaoMessageSendException() {
        super(DEFAULT_MESSAGE);
    }

    public KakaoMessageSendException(Throwable cause) {
        super(cause);
    }
}
