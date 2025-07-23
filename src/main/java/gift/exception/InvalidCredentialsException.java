package gift.exception;

public class InvalidCredentialsException extends RuntimeException {
    public enum ErrorType {
        EMAIL_NOT_FOUND,
        PASSWORD_MISMATCH
    }

    private final ErrorType errorType;

    public InvalidCredentialsException(String message, ErrorType errorType) {
        super(message);
        this.errorType = errorType;
    }

    public ErrorType getErrorType() {
        return errorType;
    }
}
