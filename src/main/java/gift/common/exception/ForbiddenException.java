package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;

public class ForbiddenException extends CustomException {

    public ForbiddenException() {
        super(CustomResponseCode.UNAUTHORIZED);
    }

    public ForbiddenException(CustomResponseCode customCode) {
        super(customCode);
    }
}
