package gift.product.exception;

public class NotEnoughInventoryException extends RuntimeException {
    public NotEnoughInventoryException(Long current, Long requested) {
        super("재고가 부족합니다. 현재수량: " + current + ", 요청수량: " + requested);
    }
}
