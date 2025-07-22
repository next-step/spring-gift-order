package gift.common.vo;

import gift.common.exception.InvalidSortDirectionException;
import org.springframework.data.domain.Sort;

public enum SortDirection {
    ASC, DESC;

    public static SortDirection from(String direction) {
        try {
            return SortDirection.valueOf(direction.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidSortDirectionException(direction);
        }
    }

    public Sort.Direction toSortDir() {
        return Sort.Direction.valueOf(this.name());
    }

}
