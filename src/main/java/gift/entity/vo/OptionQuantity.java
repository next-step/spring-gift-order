package gift.entity.vo;

import gift.exception.InvalidOptionCreateException;
import gift.exception.InvalidQuantityException;
import jakarta.persistence.Embeddable;

@Embeddable
public class OptionQuantity {

    private Integer value;

    protected OptionQuantity() {
    }

    public OptionQuantity(Integer value) {
        check(value);
        this.value = value;
    }

    private void check(Integer value) {
        if (value == null) {
            throw new InvalidOptionCreateException("수량은 필수입니다.");
        }
        if (value < 1 || value >= 100_000_000) {
            throw new InvalidOptionCreateException("수량은 최소 1개 이상 1억개 미만입니다.");
        }
    }

    public Integer value() {
        return value;
    }

    public void subtract(int quantity) {
        if (value - quantity <= 0) {
            throw new InvalidQuantityException("수량은 최소 1개 이상이어야 합니다.");
        }
        value -= quantity;
    }
}
