package gift.common.vo;

public final class PageIndex {

    private final int oneBased;

    public PageIndex(int oneBased) {
        if (oneBased < 1) {
            throw new IllegalArgumentException("페이지 인덱스는 양수이어야 합니다. 입력값: " + oneBased);
        }
        this.oneBased = oneBased;
    }

    public int toZeroBased() {
        return oneBased - 1;
    }
}
