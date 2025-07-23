package gift.common.exception;

public class WishlistNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "존재하지 않는 위시리스트입니다.";

    public WishlistNotFoundException(Long id) {
        super(DEFAULT_MESSAGE + " 위시리스트 id = " + id);
    }
}
