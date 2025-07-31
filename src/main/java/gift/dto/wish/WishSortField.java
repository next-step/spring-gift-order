package gift.dto.wish;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum WishSortField {
    ID("id"),
    PRODUCT_NAME("productName"),
    PRODUCT_PRICE("productPrice");

    private final String field;

    WishSortField(String field) {
        this.field = field;
    }

    public static Set<String> allowedFields() {
        return Stream.of(values())
            .map(WishSortField::getField)
            .collect(Collectors.toSet());
    }

    public String getField() {
        return field;
    }
}
