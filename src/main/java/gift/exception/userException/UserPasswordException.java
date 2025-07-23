package gift.exception.userException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserPasswordException extends ApplicationException {
    public UserPasswordException() {
        super(HttpStatus.BAD_REQUEST, "비밀번호는 필수 입력값입니다.");
    }
}
