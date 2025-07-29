package gift.entity.Product;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductPrice(
    @Column(name = "price", nullable = false)
    Integer price
) {

    public ProductPrice {
        if (price == null) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "상품 가격은 필수입니다.");
        }
        if (price < 0) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다.");
        }
    }
}
