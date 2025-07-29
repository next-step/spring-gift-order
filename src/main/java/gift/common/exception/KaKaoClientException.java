package gift.common.exception;

import gift.common.code.CustomResponseCode;
import gift.common.exception.core.CustomException;

public class KaKaoClientException extends CustomException {

    public KaKaoClientException() {
        super(CustomResponseCode.KAKAO_CLIENT_ERROR);
    }

    public KaKaoClientException(CustomResponseCode customCode) {
        super(customCode);
    }
}
