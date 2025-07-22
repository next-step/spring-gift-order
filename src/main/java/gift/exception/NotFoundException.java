package gift.exception;

// 레포지토리에서 Null값 받았을 때반환하는 예외
public class NotFoundException extends RuntimeException {
    public NotFoundException(String name, Long id) {
        super("Not found " + name + " with id: " + id);
    }
    public NotFoundException() {
        super("Not found");
    }
}