package gift.entity;

import gift.common.exception.core.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductOptionQuantity(
    @Column(name = "quantity", nullable = false)
    Long quantity
) {

    private static final long MAX_QUANTITY = 100_000_000L;

    public ProductOptionQuantity {
        if (quantity == null || quantity < 1 || quantity >= MAX_QUANTITY) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "수량은 1 이상 1억 미만이어야 합니다.");
        }
    }

    public ProductOptionQuantity decrease(long amount) {
        if (amount < 1) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "감소 수량은 1 이상이어야 합니다.");
        }
        if (amount > this.quantity) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "옵션 수량이 부족합니다.");
        }
        return new ProductOptionQuantity(this.quantity - amount);
    }

}
