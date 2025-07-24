package gift.exception.userException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class UserPasswordInputException extends ApplicationException {
    public UserPasswordInputException() {
        super(HttpStatus.NOT_FOUND, "비밀번호가 틀렸습니다.");
    }
}
