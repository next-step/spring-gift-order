package gift.entity.product;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public record ProductImageUrl(
    @Column(name = "image_url", nullable = false)
    String value
) {

    public ProductImageUrl {
        if (value == null || value.isBlank()) {
            throw new ValidationException("이미지 URL은 필수입니다.");
        }
    }
}
