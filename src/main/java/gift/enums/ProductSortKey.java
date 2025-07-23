package gift.enums;

import org.springframework.data.domain.Sort;

public enum ProductSortKey {
    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name")),
    PRICE_ASC(Sort.by(Sort.Direction.ASC, "price")),
    PRICE_DESC(Sort.by(Sort.Direction.DESC, "price"));

    private final Sort sort;

    ProductSortKey(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }

    public static ProductSortKey from(String value) {
        return switch (value.toLowerCase()) {
            case "name-asc" -> NAME_ASC;
            case "name-desc" -> NAME_DESC;
            case "price-asc" -> PRICE_ASC;
            case "price-desc" -> PRICE_DESC;
            default -> throw new IllegalArgumentException("유효하지 않은 정렬 기준입니다. : " + value);
        };
    }
}
