package gift.entity.product;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductName(
    @Column(name = "name", nullable = false)
    String value
) {

    private static final int MAX_NAME_LENGTH = 15;
    private static final String NAME_PATTERN = "^[\\s\\w가-힣-+()&/\\[\\]]+$";

    public ProductName {
        if (value == null || value.isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "상품 이름은 필수입니다.");
        }
        if (value.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "상품명은 15자 까지만 입력 가능합니다.");
        }
        if (!value.matches(NAME_PATTERN)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "지원하지 않는 문자가 포함되어있습니다.");
        }
    }
}
