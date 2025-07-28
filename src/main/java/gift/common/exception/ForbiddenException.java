package gift.common.exception;

import gift.common.code.CustomResponseCode;

public class ForbiddenException extends CustomException {

    public ForbiddenException() {
        super(CustomResponseCode.UNAUTHORIZED);
    }

    public ForbiddenException(CustomResponseCode customCode) {
        super(customCode);
    }
}
