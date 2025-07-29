package gift.common.exception.core;

import gift.common.code.CustomResponseCode;
import org.springframework.http.HttpStatusCode;

public class CustomException extends RuntimeException {

    private final CustomResponseCode errorCode;
    private final HttpStatusCode statusCode;
    private final String customMessage;

    public CustomException(CustomResponseCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.statusCode = errorCode.getHttpStatus();
        this.customMessage = null;
    }

    public CustomException(HttpStatusCode statusCode, String customMessage) {
        super(customMessage);
        this.errorCode = null;
        this.statusCode = statusCode;
        this.customMessage = customMessage;
    }

    public CustomResponseCode getErrorCode() {
        return errorCode;
    }

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public String getCustomMessage() {
        return customMessage;
    }
}
