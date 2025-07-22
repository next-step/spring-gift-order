package gift.item.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Quantity {

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    protected Quantity() {
    }

    public Quantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("Quantity는 null일 수 없습니다.");
        }

        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity는 1 이상이어야 합니다.");
        }

        if (quantity > 99_999_999) {
            throw new IllegalArgumentException("Quantity는 1억 미만이어야 합니다.");
        }

        this.quantity = quantity;
    }

    public Integer toValue() {
        return quantity;
    }
}
