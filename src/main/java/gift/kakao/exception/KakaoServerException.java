package gift.kakao.exception;

public class KakaoServerException extends RuntimeException {

    public KakaoServerException() {
        super("카카오 서버 오류. 잠시후 다시 시도하세요.");
    }
}
