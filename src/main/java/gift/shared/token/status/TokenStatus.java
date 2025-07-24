package gift.shared.token.status;

import org.springframework.http.HttpStatus;

public enum TokenStatus {
    TOKEN_EXPIRED("TE001", "토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN_TYPE("TE002", "토큰 타입이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
    NO_TOKEN("TE003", "토큰이 존재하지 않습니다.", HttpStatus.UNAUTHORIZED),
    NOT_AGREE("TE004", "개인정보 제공 동의를 하지 않았습니다.", HttpStatus.FORBIDDEN);

    private final String code;
    private final String message;
    private final HttpStatus status;

    TokenStatus(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getMessage(){
        return "[" + code + "] " + message;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
