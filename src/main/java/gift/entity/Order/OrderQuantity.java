package gift.entity.Order;

import gift.common.exception.core.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record OrderQuantity(
    @Column(name = "quantity", nullable = false)
    int quantity
) {

    public OrderQuantity {
        if (quantity < 1) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "주문 수량은 1 이상이어야 합니다.");
        }
    }
}
