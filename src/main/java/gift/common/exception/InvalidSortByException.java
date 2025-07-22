package gift.common.exception;

public class InvalidSortByException extends RuntimeException {

    public InvalidSortByException(String sortBy) {
        super("올바른 정렬 기준이 아닙니다: " + sortBy);
    }
}
