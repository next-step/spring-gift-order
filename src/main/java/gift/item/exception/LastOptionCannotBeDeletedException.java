package gift.item.exception;

public class LastOptionCannotBeDeletedException extends RuntimeException {

    public LastOptionCannotBeDeletedException(Long itemId) {
        super("상품의 모든 옵션을 삭제할 수 없습니다. itemId: " + itemId);
    }

}
