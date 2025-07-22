package gift.exception;

public class EncryptFailedException extends RuntimeException {
    public EncryptFailedException(String message) {
      super(message);
    }
    public EncryptFailedException() {
      super("암호화 실패");
    }
}
