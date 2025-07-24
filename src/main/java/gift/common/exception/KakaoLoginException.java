package gift.common.exception;

public class KakaoLoginException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "카카오 로그인 도중 오류가 발생하였습니다.";

    public KakaoLoginException(Exception e) {
        super(DEFAULT_MESSAGE + e.getMessage());
    }
}
