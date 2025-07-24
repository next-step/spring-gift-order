package gift.exception.itemException;

public class OptionDuplicatedException extends RuntimeException {

    public OptionDuplicatedException() {
        super("중복된 이름의 옵션입니다.");
    }
}
