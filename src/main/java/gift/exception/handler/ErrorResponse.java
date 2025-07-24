package gift.exception.handler;

import java.util.List;

public class ErrorResponse {
    private final int status;
    private final String errorCode;
    private final String message;
    private final List<FieldErrorResponse> errors;

    public ErrorResponse(int status, String errorCode, String message, List<FieldErrorResponse> errors) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
        this.errors = errors;
    }

    public int getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }

    public List<FieldErrorResponse> getErrors() {
        return errors;
    }
}
