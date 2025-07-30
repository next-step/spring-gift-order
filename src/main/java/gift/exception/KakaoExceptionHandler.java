package gift.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class KakaoExceptionHandler {

    private ResponseEntity<ErrorResponse> responseEntityBuild(KakaoErrorCode kakaoErrorCode,
        HttpStatus status) {
        return new ResponseEntity<>(ErrorResponse.of(kakaoErrorCode), status);
    }

    @ExceptionHandler(KakaoClientException.class)
    public ResponseEntity<ErrorResponse> handleKakaoClientException(
        KakaoClientException exception
    ) {
        return switch (exception.getCode()) {
            case -1 -> responseEntityBuild(KakaoErrorCode.SERVER_INTERNAL_ERROR,
                HttpStatus.BAD_REQUEST);
            case -2 -> responseEntityBuild(KakaoErrorCode.INVALID_PARAMETER,
                HttpStatus.BAD_REQUEST);
            case -3 -> responseEntityBuild(KakaoErrorCode.FEATURE_NOT_ENABLED,
                HttpStatus.FORBIDDEN);
            case -4 -> responseEntityBuild(KakaoErrorCode.ACCOUNT_RESTRICTED,
                HttpStatus.FORBIDDEN);
            case -5 -> responseEntityBuild(KakaoErrorCode.NO_API_PERMISSION,
                HttpStatus.FORBIDDEN);
            case -6 -> responseEntityBuild(KakaoErrorCode.ACTION_NOT_ALLOWED,
                HttpStatus.FORBIDDEN);
            case -7 -> responseEntityBuild(KakaoErrorCode.SERVICE_MAINTENANCE,
                HttpStatus.BAD_REQUEST);
            case -8 -> responseEntityBuild(KakaoErrorCode.INVALID_HEADER,
                HttpStatus.BAD_REQUEST);
            case -9 -> responseEntityBuild(KakaoErrorCode.API_DEPRECATED,
                HttpStatus.BAD_REQUEST);
            case -10 -> responseEntityBuild(KakaoErrorCode.QUOTA_EXCEEDED,
                HttpStatus.BAD_REQUEST);
            case -401 -> responseEntityBuild(KakaoErrorCode.INVALID_TOKEN,
                HttpStatus.UNAUTHORIZED);
            case -501 -> responseEntityBuild(KakaoErrorCode.NOT_KAKAOTALK_USER,
                HttpStatus.BAD_REQUEST);
            case -602 -> responseEntityBuild(KakaoErrorCode.IMAGE_SIZE_EXCEEDED,
                HttpStatus.BAD_REQUEST);
            case -603 -> responseEntityBuild(KakaoErrorCode.REQUEST_TIMEOUT,
                HttpStatus.BAD_REQUEST);
            case -606 -> responseEntityBuild(KakaoErrorCode.IMAGE_COUNT_EXCEEDED,
                HttpStatus.BAD_REQUEST);
            case -903 -> responseEntityBuild(KakaoErrorCode.UNREGISTERED_APP_KEY,
                HttpStatus.BAD_REQUEST);
            case -911 -> responseEntityBuild(KakaoErrorCode.UNSUPPORTED_IMAGE_FORMAT,
                HttpStatus.BAD_REQUEST);
            case -999 -> responseEntityBuild(KakaoErrorCode.EMPTY_ACCESS_TOKEN,
                HttpStatus.BAD_GATEWAY);

            case -101 -> responseEntityBuild(KakaoErrorCode.UNCONNECTED_ACCOUNT,
                HttpStatus.BAD_REQUEST);
            case -102 -> responseEntityBuild(KakaoErrorCode.ALREADY_CONNECTED,
                HttpStatus.BAD_REQUEST);
            case -103 -> responseEntityBuild(KakaoErrorCode.DORMANT_OR_NONEXISTENT,
                HttpStatus.BAD_REQUEST);
            case -201 -> responseEntityBuild(KakaoErrorCode.INVALID_USER_PROPERTY_KEY,
                HttpStatus.BAD_REQUEST);
            case -402 -> responseEntityBuild(KakaoErrorCode.SCOPE_NOT_GRANTED,
                HttpStatus.FORBIDDEN);
            case -406 -> responseEntityBuild(KakaoErrorCode.UNDER_14_RESTRICTED,
                HttpStatus.UNAUTHORIZED);

            default -> responseEntityBuild(KakaoErrorCode.UNKNOWN_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(KakaoServerException.class)
    public ResponseEntity<ErrorResponse> handleKakaoServerException(
        KakaoServerException exception
    ) {
        return switch (exception.getCode()) {
            case -9798 -> responseEntityBuild(KakaoErrorCode.SERVICE_CHECK,
                HttpStatus.SERVICE_UNAVAILABLE);

            default -> responseEntityBuild(KakaoErrorCode.UNKNOWN_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR);
        };
    }

    @ExceptionHandler(KakaoSendMessageException.class)
    public ResponseEntity<ErrorResponse> handleKakaoSendMessageException() {
        return responseEntityBuild(KakaoErrorCode.KAKAO_MESSAGE_ERROR,
            HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
