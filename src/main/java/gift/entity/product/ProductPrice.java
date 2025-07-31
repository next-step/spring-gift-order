package gift.entity.product;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductPrice(
    @Column(name = "price", nullable = false)
    Integer value
) {

    public ProductPrice {
        if (value == null) {
            throw new ValidationException("상품 가격은 필수입니다.");
        }
        if (value < 0) {
            throw new ValidationException("상품 가격은 0 이상이어야 합니다.");
        }
    }
}
