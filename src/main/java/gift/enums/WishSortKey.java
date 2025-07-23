package gift.enums;

import org.springframework.data.domain.Sort;

public enum WishSortKey {
    PRODUCT_NAME_ASC(Sort.by(Sort.Direction.ASC, "product.name")),
    PRODUCT_NAME_DESC(Sort.by(Sort.Direction.DESC, "product.name")),
    PRODUCT_PRICE_ASC(Sort.by(Sort.Direction.ASC, "product.price")),
    PRODUCT_PRICE_DESC(Sort.by(Sort.Direction.DESC, "product.price"));

    private final Sort sort;

    WishSortKey(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }

    public static WishSortKey from(String value) {
        return switch (value.toLowerCase()) {
            case "name-asc" -> PRODUCT_NAME_ASC;
            case "name-desc" -> PRODUCT_NAME_DESC;
            case "price-asc" -> PRODUCT_PRICE_ASC;
            case "price-desc" -> PRODUCT_PRICE_DESC;
            default -> throw new IllegalArgumentException("유효하지 않은 정렬 기준입니다. : " + value);
        };
    }
}
