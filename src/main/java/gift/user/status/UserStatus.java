package gift.user.status;

import org.springframework.http.HttpStatus;

public enum UserStatus {
    NO_USER("UE001", "해당 User 를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_PASSWORD("UE002", "비밀번호가 맞지 않습니다.", HttpStatus.FORBIDDEN),
    NO_VALUE("GE003", "수정할 값이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);

    private final String code;
    private final String message;
    private final HttpStatus status;

    UserStatus(String code, String message, HttpStatus status) {
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
