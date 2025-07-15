package gift.exception;

public record FieldErrorResponseDto(int status, String errorCode, String message, String field) {

    public static FieldErrorResponseDto of(int status, String errorCode, String message, String field) {
        return new FieldErrorResponseDto(status, errorCode, message, field);
    }

}
