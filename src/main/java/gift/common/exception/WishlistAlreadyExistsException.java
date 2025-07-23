package gift.common.exception;

public class WishlistAlreadyExistsException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "이미 위시리스트에 추가된 상품입니다.";

    public WishlistAlreadyExistsException() {
        super(DEFAULT_MESSAGE);
    }
}
