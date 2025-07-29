package gift.entity.Product.Option;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductOptionName(
    @Column(name = "name", nullable = false)
    String name
) {

    private static final int MAX_NAME_LENGTH = 50;
    private static final String NAME_PATTERN = "^[\\p{L}0-9()\\[\\]+\\-&/_ ]+$";

    public ProductOptionName {
        if (name == null || name.isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "옵션 이름은 필수입니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "옵션 이름은 50자 이하여야 합니다.");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "옵션 이름에 허용되지 않은 문자가 포함되어 있습니다.");
        }
    }

}