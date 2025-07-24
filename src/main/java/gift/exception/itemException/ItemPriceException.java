package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ItemPriceException extends ApplicationException {

    public ItemPriceException() {
        super(HttpStatus.BAD_REQUEST, "상품 가격은 0 이상이어야합니다.");
    }
}
