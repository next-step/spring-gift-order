package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatus;

public class ValidationException extends CustomException {

    public ValidationException() {
        super(CustomResponseCode.VALIDATION_FAILED);
    }

    public ValidationException(CustomResponseCode customCode) {
        super(customCode);
    }

    public ValidationException(String customMessage) {
        super(HttpStatus.BAD_REQUEST, customMessage);
    }
}
