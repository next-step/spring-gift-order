package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;

public class ValidationException extends CustomException {

    public ValidationException() {
        super(CustomResponseCode.VALIDATION_FAILED);
    }

    public ValidationException(CustomResponseCode customCode) {
        super(customCode);
    }
}