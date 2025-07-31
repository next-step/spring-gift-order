package gift.exception.orderException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class OrderQuantityStockOverException extends ApplicationException {

    public OrderQuantityStockOverException() {
        super(HttpStatus.BAD_REQUEST, "재고가 부족합니다.");
    }
}
