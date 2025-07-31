package gift.entity.wish;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record WishQuantity(
    @Column(name = "quantity", nullable = false)
    Integer value
) {

    public WishQuantity {
        if (value == null || value < 0) {
            throw new ValidationException("수량은 0 이상이어야 합니다.");
        }
    }
}
