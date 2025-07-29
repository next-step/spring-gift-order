package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;
import org.springframework.http.HttpStatusCode;

public class ValidationException extends CustomException {

    public ValidationException() {
        super(CustomResponseCode.VALIDATION_FAILED);
    }

    public ValidationException(CustomResponseCode customCode) {
        super(customCode);
    }

    public ValidationException(HttpStatusCode statusCode, String customMessage) {
        super(statusCode, customMessage);
    }
}
