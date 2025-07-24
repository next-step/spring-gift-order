package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class WishItemNotFoundException extends ApplicationException {

    public WishItemNotFoundException() {
        super(HttpStatus.NOT_FOUND,"해당 옵션은 없습니다");
    }
}
