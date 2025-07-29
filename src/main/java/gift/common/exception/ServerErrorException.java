package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;

public class ServerErrorException extends CustomException {

    public ServerErrorException() {
        super(CustomResponseCode.SERVER_ERROR);
    }

    public ServerErrorException(CustomResponseCode customCode) {
        super(customCode);
    }
}
