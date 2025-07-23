package gift.common.exception;

public class NotEnoughQuantityException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "옵션 수량이 부족합니다.";

    public NotEnoughQuantityException(int quantity) {
        super(DEFAULT_MESSAGE + "상품의 남은 수량: " + quantity);
    }
}
