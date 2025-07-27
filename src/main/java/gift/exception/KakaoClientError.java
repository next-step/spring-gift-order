package gift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

public enum KakaoClientError {
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "인증 오류가 발생했습니다."),
    FORBIDDEN(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    DEFAULT("카카오 인증 중 클라이언트 오류가 발생했습니다.");

    private HttpStatus status;
    private final String message;

    KakaoClientError(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    KakaoClientError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public static KakaoClientError from(HttpStatusCode statusCode) {
        return switch (statusCode.value()) {
            case 400 -> BAD_REQUEST;
            case 401 -> UNAUTHORIZED;
            case 403 -> FORBIDDEN;
            default -> DEFAULT;
        };
    }
}
