package gift.entity.Wish;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record WishQuantity(
    @Column(name = "quantity", nullable = false)
    Integer quantity
) {

    public WishQuantity {
        if (quantity == null || quantity < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "수량은 0 이상이어야 합니다.");
        }
    }
}
