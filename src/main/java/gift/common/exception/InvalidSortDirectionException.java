package gift.common.exception;

public class InvalidSortDirectionException extends RuntimeException {

    public InvalidSortDirectionException(String direction) {
        super("올바른 정렬 방향이 아닙니다: " + direction + ", asc / desc 중 하나가 필요합니다.");
    }
}
