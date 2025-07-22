package gift.exception;

public class DecryptFailedException extends RuntimeException {
  public DecryptFailedException(String message) {
    super(message);
  }
  public DecryptFailedException() {
    super("복호화 실패");
  }
}
