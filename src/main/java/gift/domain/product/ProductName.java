package gift.domain.product;

import jakarta.persistence.Embeddable;
import java.util.Objects;

@Embeddable
public class ProductName {
    private static final int MAX_PRODUCT_NAME_LENGTH = 15;
    private static final String ALLOWED_NAME_PATTERN = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ()\\[\\]+\\-&/_\\s]*$";
    private static final String ALLOWED_CHAR_PATTERN = "[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ()\\[\\]+\\-&/_\\s]";

    private String productName;

    protected ProductName() {}

    public ProductName(String productName) {
        validate(productName);
        this.productName = productName;
    }

    private void validate(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("상품명은 비어 있을 수 없습니다.");
        }
        if (name.length() > MAX_PRODUCT_NAME_LENGTH) {
            throw new IllegalArgumentException("상품명은 " + MAX_PRODUCT_NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
        if (!name.matches(ALLOWED_NAME_PATTERN)){
            String invalidChars = name.chars()
                    .mapToObj(c -> (char) c)
                    .filter(c -> !String.valueOf(c).matches(ALLOWED_CHAR_PATTERN))
                    .distinct()
                    .map(String::valueOf)
                    .reduce("", (a, b) -> a + b);
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다: [" + invalidChars + "]");
        }
    }

    public String getValue() {
        return productName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductName)) return false;
        ProductName that = (ProductName) o;
        return Objects.equals(productName, that.productName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productName);
    }
}
