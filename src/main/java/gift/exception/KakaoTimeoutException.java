package gift.exception;

public class KakaoTimeoutException extends RuntimeException {
  private final int code;

  public KakaoTimeoutException(int code, String message) {
    super(message);
    this.code = code;
  }

  public KakaoTimeoutException(String message) {
    this(-20000, message);
  }

  public int getCode() {
    return code;
  }
}
