package gift.common.code;

import org.springframework.http.HttpStatus;

public enum CustomResponseCode {

    CREATED("생성 성공", HttpStatus.CREATED),
    RETRIEVED("조회 성공", HttpStatus.OK),
    UPDATED("수정 성공", HttpStatus.OK),
    DELETED("삭제 성공", HttpStatus.NO_CONTENT),
    LIST_RETRIEVED("목록 조회 성공", HttpStatus.OK),

    LOGIN_SUCCESS("로그인 성공!", HttpStatus.OK),
    VALIDATION_FAILED("요청 값이 유효하지 않습니다.", HttpStatus.BAD_REQUEST),
    UNAUTHORIZED("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED),
    INVALID_TOKEN("유효하지 않은 토큰입니다.", HttpStatus.UNAUTHORIZED),
    LOGIN_FAILED("로그인에 실패!", HttpStatus.FORBIDDEN),
    FORBIDDEN_KEYWORD("%s - 해당 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다.", HttpStatus.FORBIDDEN),
    NOT_FOUND("리소스를 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    EMAIL_DUPLICATE("중복된 이메일이 이미 존재합니다。", HttpStatus.CONFLICT),
    ALREADY_EXISTS("이미 등록된 항목입니다.", HttpStatus.CONFLICT),
    INVALID_SORT_FIELD("허용되지 않는 정렬 필드입니다.", HttpStatus.BAD_REQUEST),
    INVALID_SORT_DIRECTION("허용되지 않는 정렬 방향입니다.", HttpStatus.BAD_REQUEST),
    DB_ERROR("데이터베이스 처리 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_ERROR("서버 내부 오류입니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    OPTION_NAME_REQUIRED("옵션 이름은 필수입니다.", HttpStatus.BAD_REQUEST),
    OPTION_NAME_TOO_LONG("옵션 이름은 50자 이하여야 합니다.", HttpStatus.BAD_REQUEST),
    OPTION_NAME_INVALID_CHAR("옵션 이름에 허용되지 않은 문자가 포함되어 있습니다.", HttpStatus.BAD_REQUEST),
    OPTION_QUANTITY_INVALID("수량은 1 이상 1억 미만이어야 합니다.", HttpStatus.BAD_REQUEST),
    OPTION_REQUIRED("상품에는 최소 1개의 옵션이 필요합니다.", HttpStatus.BAD_REQUEST),
    OPTION_DUPLICATED("동일한 상품 내 옵션 이름은 중복될 수 없습니다.", HttpStatus.CONFLICT),
    OPTION_DECREASE_AMOUNT_INVALID("감소 수량은 1 이상이어야 합니다.", HttpStatus.BAD_REQUEST),
    OPTION_INSUFFICIENT_STOCK("옵션 수량이 부족합니다.", HttpStatus.BAD_REQUEST);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    CustomResponseCode(String message, HttpStatus httpStatus) {
        this.code = httpStatus.value();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
