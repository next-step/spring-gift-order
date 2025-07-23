package gift.global.exception;

public class NotFoundException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;

    public NotFoundException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }

    public String getMessage(){
        return this.message;
    }

    public static NotFoundException from(ErrorCode errorCode){
        return new NotFoundException(
            errorCode,
            errorCode.getErrorMessage()
        );
    }

}
