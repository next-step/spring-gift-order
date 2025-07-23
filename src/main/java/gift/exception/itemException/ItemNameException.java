package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ItemNameException extends ApplicationException {
    public ItemNameException() {
        super(HttpStatus.BAD_REQUEST, "상품명란은 필수 입니다.");
    }
}
