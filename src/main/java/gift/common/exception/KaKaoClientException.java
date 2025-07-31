package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class KaKaoClientException extends CustomException {

    public KaKaoClientException() {
        super();
    }

    public KaKaoClientException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_GATEWAY;
    }

    @Override
    public String getDefaultMessage() {
        return "카카오 API 호출 중 오류가 발생했습니다.";
    }
}
