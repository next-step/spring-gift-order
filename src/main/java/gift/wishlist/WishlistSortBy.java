package gift.wishlist;

import gift.common.exception.InvalidSortByException;
import java.util.Arrays;

public enum WishlistSortBy {
    ITEM_NAME("itemName", "item.name"),
    ITEM_PRICE("itemPrice", "item.price"),
    CREATED_AT("createdAt", "createdAt"),
    WISHLIST_ID("id", "id");

    private final String key; // 사용자가 요청할 때 보내는 key
    private final String property; // 실제 JPA 정렬 필드

    WishlistSortBy(String key, String property) {
        this.key = key;
        this.property = property;
    }

    public String property() {
        return property;
    }

    public static WishlistSortBy from(String key) {
        return Arrays.stream(values())
            .filter(e -> e.key.equalsIgnoreCase(key))
            .findFirst()
            .orElseThrow(() -> new InvalidSortByException(key));
    }

}
