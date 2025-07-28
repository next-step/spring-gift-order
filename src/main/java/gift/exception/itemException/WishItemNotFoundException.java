package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class WishItemNotFoundException extends ApplicationException {

    public WishItemNotFoundException() {
        super(HttpStatus.NOT_FOUND, "해당 위시리스트는 없습니다");
    }
}
