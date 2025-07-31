package gift.common.exception;

import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class MemberNotFoundException extends CustomException {

    public MemberNotFoundException() {
        super();
    }

    public MemberNotFoundException(String message) {
        super(message);
    }

    @Override
    public HttpStatus getStatus() {
        return HttpStatus.NOT_FOUND;
    }

    @Override
    public String getDefaultMessage() {
        return "해당 회원을 찾을 수 없습니다.";
    }
}
