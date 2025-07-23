package gift.global.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    // not found
    NOT_EXISTS(HttpStatus.NOT_FOUND, "객체를 찾을 수 없습니다."),

    // invalid request
    INVALID_KAKAO_NAME(HttpStatus.BAD_REQUEST, "'카카오'가 들어간 상품명은 관리자에게 문의해 주세요."),
    INVALID_FORM_REQUEST(HttpStatus.BAD_REQUEST, "허용되지 않은 값을 입력하셨습니다."),
    INVALID_TOKEN_REQUEST(HttpStatus.UNAUTHORIZED, "잘못된 형식의 토큰입니다."),
    INCORRECT_LOGIN_INFO(HttpStatus.FORBIDDEN, "이메일, 비밀번호 조합이 잘못되었습니다."),
    INVALID_SORT_NAMES(HttpStatus.BAD_REQUEST, "정렬 조건을 다시 확인해 주세요"),
    INVALID_SUBTRACT_AMOUNT(HttpStatus.BAD_REQUEST, "빼려는 수량은 0보다 커야 합니다."),
    INSUFFICIENT_QUANTITY(HttpStatus.BAD_REQUEST, "옵션의 수량은 0 이하가 될 수 없습니다."),


    // already exist
    DUPLICATE_EMAIL(HttpStatus.FORBIDDEN, "이미 사용 중인 이메일입니다."),
    ALREADY_EXISTS_NAME(HttpStatus.BAD_REQUEST, "이미 존재하는 이름입니다."),

    // others
    OTHERS(HttpStatus.INTERNAL_SERVER_ERROR, "기타 오류입니다.");


    private final HttpStatus httpStatus;
    private final String errorMessage;

    ErrorCode(HttpStatus httpStatus, String errorMessage) {
        this.httpStatus = httpStatus;
        this.errorMessage = errorMessage;
    }

    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
