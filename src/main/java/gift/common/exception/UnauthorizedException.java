package gift.common.exception;

import gift.common.code.CustomResponseCode;

public class UnauthorizedException extends CustomException {

    public UnauthorizedException() {
        super(CustomResponseCode.UNAUTHORIZED);
    }

    public UnauthorizedException(CustomResponseCode customCode) {
        super(customCode);
    }
}
