package gift.common.exception;

public class ProductOptionException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "상품에는 반드시 하나의 옵션은 포함되어야 합니다.";

    public ProductOptionException() {
        super(DEFAULT_MESSAGE);
    }
}
