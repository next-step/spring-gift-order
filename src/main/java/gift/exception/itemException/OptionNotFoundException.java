package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OptionNotFoundException extends ApplicationException {
    public OptionNotFoundException() {
        super(HttpStatus.NOT_FOUND,"해당 옵션을 찾을 수 없습니다.");
    }
}
