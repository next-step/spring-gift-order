package gift.entity.Product;

import gift.common.exception.core.CustomException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record ProductName(
    @Column(name = "name", nullable = false)
    String name
) {

    private static final int MAX_NAME_LENGTH = 15;
    private static final String NAME_PATTERN = "^[\\s\\w가-힣-+()&/\\[\\]]+$";

    public ProductName {
        if (name == null || name.isBlank()) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "상품 이름은 필수입니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "상품명은 15자 까지만 입력 가능합니다.");
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "지원하지 않는 문자가 포함되어있습니다.");
        }
    }
}
