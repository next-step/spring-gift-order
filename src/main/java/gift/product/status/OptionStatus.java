package gift.product.status;

import org.springframework.http.HttpStatus;

public enum OptionStatus {
    OVER_QUANTITY("OE001", "기존의 수량보다 많은 수량을 요청하였습니다.", HttpStatus.BAD_REQUEST),
    SAME_NAME("OE002", "같은 상품 내에 동일한 이름의 옵션이 존재합니다.", HttpStatus.BAD_REQUEST),;

    private final String code;
    private final String message;
    private final HttpStatus status;

    OptionStatus(String code, String message, HttpStatus status) {
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
