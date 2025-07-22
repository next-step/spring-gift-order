package gift.dto;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public enum ProductSortField {
    ID("id"),
    NAME("name"),
    PRICE("price");

    private final String field;

    ProductSortField(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public static Set<String> allowedFields() {
        return Stream.of(values())
            .map(ProductSortField::getField)
            .collect(Collectors.toSet());
    }
}

