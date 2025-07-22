package gift.item;

import gift.common.exception.InvalidSortByException;
import java.util.Arrays;

public enum ItemSortBy {
    ITEM_NAME("name", "name"),
    ITEM_PRICE("price", "price"),
    ITEM_ID("id", "id");

    private final String key; // 사용자가 요청할 때 보내는 key
    private final String property; // 실제 JPA 정렬 필드

    ItemSortBy(String key, String property) {
        this.key = key;
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static ItemSortBy from(String key) {
        return Arrays.stream(values())
            .filter(e -> e.key.equalsIgnoreCase(key))
            .findFirst()
            .orElseThrow(() -> new InvalidSortByException(key));
    }

}
