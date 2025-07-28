package gift.exception.orderException;

import gift.exception.ApplicationException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;

public class OrderQuantityException extends ApplicationException {
    public OrderQuantityException() {
        super(HttpStatus.BAD_REQUEST,"최대 수량은 1개부터 100,000,000개 까지입니다.");
    }

}
