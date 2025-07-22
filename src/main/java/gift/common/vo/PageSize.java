package gift.common.vo;

public final class PageSize {

    private final int pageSize;

    public PageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException("페이지 사이즈는 양수이어야 합니다. 입력값: " + pageSize);
        }
        this.pageSize = pageSize;
    }

    public int toValue() {
        return pageSize;
    }
}