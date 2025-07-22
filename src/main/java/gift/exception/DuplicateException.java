package gift.exception;

public class DuplicateException extends RuntimeException {
    public DuplicateException(String message) {
        super(message);
    }
    public DuplicateException() { super("중복된 요청입니다."); }
}
