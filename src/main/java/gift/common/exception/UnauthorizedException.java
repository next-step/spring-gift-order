package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super(CustomResponseCode.UNAUTHORIZED);
    }

    public UnauthorizedException(CustomResponseCode customCode) {
        super(customCode);
    }
}
