package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OptionExceptionException extends ApplicationException {
    public OptionExceptionException() {
        super(HttpStatus.BAD_REQUEST, "옵션명은 영문, 숫자, 한글, 공백, 특수문자 (), [], +, -, &, /, _ 만 가능하며 1~50자여야 합니다.");
    }
}
