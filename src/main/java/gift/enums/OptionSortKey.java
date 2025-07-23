package gift.enums;

import org.springframework.data.domain.Sort;

public enum OptionSortKey {

    NAME_ASC(Sort.by(Sort.Direction.ASC, "name")),
    NAME_DESC(Sort.by(Sort.Direction.DESC, "name"));

    private final Sort sort;

    OptionSortKey(Sort sort) {
        this.sort = sort;
    }

    public Sort getSort() {
        return sort;
    }

    public static OptionSortKey from(String value) {
        return switch (value.toLowerCase()) {
            case "name-asc" -> NAME_ASC;
            case "name-desc" -> NAME_DESC;
            default -> throw new IllegalArgumentException("유효하지 않은 정렬 기준입니다. : " + value);
        };
    }
}
