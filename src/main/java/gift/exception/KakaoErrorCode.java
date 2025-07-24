package gift.exception;

import org.springframework.http.HttpStatus;

public enum KakaoErrorCode {
    SERVER_INTERNAL_ERROR(HttpStatus.BAD_REQUEST, "-1", "서버 내부에서 처리 중에 에러가 발생하였습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "-2", "필수 인자가 누락되었거나, 인자값이 올바르지 않습니다."),
    FEATURE_NOT_ENABLED(HttpStatus.FORBIDDEN, "-3", "해당 API 사용을 위한 기능이 활성화되지 않았습니다."),
    ACCOUNT_RESTRICTED(HttpStatus.FORBIDDEN, "-4", "계정이 제재되었습니다."),
    NO_API_PERMISSION(HttpStatus.FORBIDDEN, "-5", "해당 API에 대한 요청 권한이 없습니다."),
    ACTION_NOT_ALLOWED(HttpStatus.FORBIDDEN, "-6", "카카오 서비스에서 허용하지 않는 동작입니다."),
    SERVICE_MAINTENANCE(HttpStatus.BAD_REQUEST, "-7", "서비스 점검 또는 내부 문제가 있습니다."),
    INVALID_HEADER(HttpStatus.BAD_REQUEST, "-8", "올바르지 않은 헤더로 요청하였습니다."),
    API_DEPRECATED(HttpStatus.BAD_REQUEST, "-9", "서비스가 종료된 API입니다."),
    QUOTA_EXCEEDED(HttpStatus.BAD_REQUEST, "-10", "허용된 요청 회수를 초과하였습니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "-401", "유효하지 않은 앱키 또는 액세스 토큰입니다."),
    NOT_KAKAOTALK_USER(HttpStatus.BAD_REQUEST, "-501", "카카오톡 미가입 또는 유예 사용자입니다."),
    IMAGE_SIZE_EXCEEDED(HttpStatus.BAD_REQUEST, "-602", "이미지 최대 용량을 초과하였습니다."),
    REQUEST_TIMEOUT(HttpStatus.BAD_REQUEST, "-603", "카카오 플랫폼 내부에서 요청 처리 중 타임아웃이 발생하였습니다."),
    IMAGE_COUNT_EXCEEDED(HttpStatus.BAD_REQUEST, "-606", "업로드할 수 있는 최대 이미지 개수를 초과하였습니다."),
    UNREGISTERED_APP_KEY(HttpStatus.BAD_REQUEST, "-903", "등록되지 않은 앱키입니다."),
    UNSUPPORTED_IMAGE_FORMAT(HttpStatus.BAD_REQUEST, "-911", "지원하지 않는 이미지 포맷입니다."),
    EMPTY_ACCESS_TOKEN(HttpStatus.BAD_GATEWAY, "-999", "카카오 토큰이 비어있습니다."),
    SERVICE_CHECK(HttpStatus.SERVICE_UNAVAILABLE, "-9798", "서비스 점검 중입니다."),
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "-9999", "알 수 없는 카카오 오류가 발생했습니다."),

    UNCONNECTED_ACCOUNT(HttpStatus.BAD_REQUEST, "-101", "해당 앱에 카카오계정 연결이 완료되지 않은 사용자입니다."),
    ALREADY_CONNECTED(HttpStatus.BAD_REQUEST, "-102", "이미 앱과 연결된 계정의 토큰으로 요청하였습니다."),
    DORMANT_OR_NONEXISTENT(HttpStatus.BAD_REQUEST, "-103", "휴면 상태이거나 존재하지 않는 카카오계정입니다."),
    INVALID_USER_PROPERTY_KEY(HttpStatus.BAD_REQUEST, "-201",
        "앱에 추가하지 않은 사용자 프로퍼티 키를 요청·저장하려고 했습니다."),
    SCOPE_NOT_GRANTED(HttpStatus.FORBIDDEN, "-402", "해당 리소스에 대한 사용자 동의가 필요합니다."),
    UNDER_14_RESTRICTED(HttpStatus.UNAUTHORIZED, "-406", "14세 미만 사용자는 호출할 수 없는 API입니다.");

    private HttpStatus status;
    private String errorCode;
    private String message;

    KakaoErrorCode(HttpStatus status, String errorCode, String message) {
        this.status = status;
        this.errorCode = errorCode;
        this.message = message;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public String getMessage() {
        return message;
    }
}
