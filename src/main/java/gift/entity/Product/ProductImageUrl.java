package gift.entity.Product;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductImageUrl(
    @Column(name = "image_url", nullable = false)
    String value
) {

    public ProductImageUrl {
        if (value == null || value.isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "이미지 URL은 필수입니다.");
        }
    }
}
