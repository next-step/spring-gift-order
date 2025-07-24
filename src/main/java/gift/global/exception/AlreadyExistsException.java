package gift.global.exception;

public class AlreadyExistsException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;

    public AlreadyExistsException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }

    public String getMessage(){
        return this.message;
    }

    public static AlreadyExistsException from(ErrorCode errorCode){
        return new AlreadyExistsException(
            errorCode,
            errorCode.getErrorMessage()
        );
    }

}
