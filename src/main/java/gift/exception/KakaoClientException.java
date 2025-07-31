package gift.exception;

public class KakaoClientException extends KakaoException {

  private final int code;

  public KakaoClientException(int code, String message) {
    super(message);
    this.code = code;
  }

  public KakaoClientException(String message) {
    this(-999, message);
  }

  public int getCode() {
    return code;
  }
}
