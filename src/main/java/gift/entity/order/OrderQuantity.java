package gift.entity.order;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record OrderQuantity(
    @Column(name = "quantity", nullable = false)
    int value
) {

    public OrderQuantity {
        if (value < 1) {
            throw new ValidationException("주문 수량은 1 이상이어야 합니다.");
        }
    }
}
