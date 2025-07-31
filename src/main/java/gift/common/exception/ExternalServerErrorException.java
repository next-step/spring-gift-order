package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class ExternalServerErrorException extends CustomException {

    public ExternalServerErrorException() {
        super();
    }

    public ExternalServerErrorException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.BAD_GATEWAY;
    }

    @Override
    public String getDefaultMessage() {
        return "외부 API 서버에서 오류가 발생했습니다.";
    }
}
