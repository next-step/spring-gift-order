package gift.constant;

public enum PageSize {
    PAGE_SIZE(10);

    private final int size;

    PageSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
