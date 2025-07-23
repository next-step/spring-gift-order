package gift.exception.userException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserEmailException extends ApplicationException {
    public UserEmailException() {
        super(HttpStatus.BAD_REQUEST, "이메일은 필수 입력값입니다.");
    }
}
