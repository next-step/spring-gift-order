package gift.entity.product.option;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductOptionQuantity(
    @Column(name = "quantity", nullable = false)
    Long value
) {

    private static final long MAX_QUANTITY = 100_000_000L;

    public ProductOptionQuantity {
        if (value == null || value < 1 || value >= MAX_QUANTITY) {
            throw new ValidationException("수량은 1 이상 1억 미만이어야 합니다.");
        }
    }

    public ProductOptionQuantity decrease(long amount) {
        if (amount < 1) {
            throw new ValidationException("감소 수량은 1 이상이어야 합니다.");
        }
        if (amount > this.value) {
            throw new ValidationException("옵션 수량이 부족합니다.");
        }
        return new ProductOptionQuantity(this.value - amount);
    }

}
