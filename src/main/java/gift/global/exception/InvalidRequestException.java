package gift.global.exception;

public class InvalidRequestException extends RuntimeException{
    private final ErrorCode errorCode;
    private final String message;

    public InvalidRequestException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public ErrorCode getErrorCode(){
        return this.errorCode;
    }

    public String getMessage(){
        return this.message;
    }

    public static InvalidRequestException from(ErrorCode errorCode){
        return new InvalidRequestException(
            errorCode,
            errorCode.getErrorMessage()
        );
    }

}
