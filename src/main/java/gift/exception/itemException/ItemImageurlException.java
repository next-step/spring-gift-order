package gift.exception.itemException;

import gift.exception.ApplicationException;
import org.springframework.http.HttpStatus;

public class ItemImageurlException extends ApplicationException {
    public ItemImageurlException() {
        super(HttpStatus.BAD_REQUEST, "이미지 파일은 필수입니다.");
    }
}
