package gift.common.exception;

import java.net.URI;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;

public class ErrorResponseDto extends ProblemDetail {

    /**
     * type을 설정하지 않은 ProblemDetail을 생성
     *
     * @param status   http 상태 코드
     * @param detail   상세 설명
     * @param instance 예외 발생한 URI
     */
    public ErrorResponseDto(HttpStatus status, String detail, URI instance) {
        super(status.value());
        this.setDetail(detail);
        this.setInstance(instance);

        // RFC 9457의 권고에 따라 type이 `about:blank`일 때 title을 HTTP status phrase와 동일하게 지정
        this.setTitle(status.getReasonPhrase());
    }

}
