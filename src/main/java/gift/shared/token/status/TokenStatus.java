package gift.shared.token.status;

import org.springframework.http.HttpStatus;

public enum TokenStatus {
    TOKEN_EXPIRED("TE001", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE("TE002", "토큰 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_TOKEN("TE003", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED);

    private final String code;
    private final String message;
    private final HttpStatus status;

    TokenStatus(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getErrorMessage(){
        return "[" + code + "] " + message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
