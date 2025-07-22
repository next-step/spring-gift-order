package gift.exception;
import java.util.List;

public class InvalidProductNameException extends RuntimeException {
  public InvalidProductNameException(List<String> message) {
    super(message + "가 포함된 제품명은 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
  }
  public InvalidProductNameException() {
    super("해당 제품명은 담당 MD와 협의한 경우에만 사용할 수 있습니다.");
  }
}
