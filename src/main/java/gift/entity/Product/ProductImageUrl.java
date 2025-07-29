package gift.entity.Product;

import gift.common.exception.core.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductImageUrl(
    @Column(name = "image_url", nullable = false)
    String imageUrl
) {

    public ProductImageUrl {
        if (imageUrl == null || imageUrl.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "이미지 URL은 필수입니다.");
        }
    }
}
