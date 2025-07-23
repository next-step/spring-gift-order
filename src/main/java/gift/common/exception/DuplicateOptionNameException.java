package gift.common.exception;

public class DuplicateOptionNameException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "중복된 옵션 이름입니다.";

    public DuplicateOptionNameException() {
        super(DEFAULT_MESSAGE);
    }
}
