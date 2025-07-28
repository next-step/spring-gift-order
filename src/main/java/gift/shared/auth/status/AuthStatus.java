package gift.shared.auth.status;

import org.springframework.http.HttpStatus;

public enum AuthStatus {
    ALGORITHM_MISS("AE001", "내부 알고리즘이 정상적으로 작동되지 않았습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    AuthStatus(String code, String message, HttpStatus status) {
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
