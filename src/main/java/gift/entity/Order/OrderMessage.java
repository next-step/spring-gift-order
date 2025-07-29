package gift.entity.Order;

import gift.common.exception.ValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import org.springframework.http.HttpStatus;

@Embeddable
public record OrderMessage(
    @Column(name = "message", length = 500)
    String message
) {

    private static final int MAX_LENGTH = 500;

    public OrderMessage {
        if (message == null || message.isBlank()) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "주문 메시지는 필수입니다.");
        }
        if (message.length() > MAX_LENGTH) {
            throw new ValidationException(HttpStatus.BAD_REQUEST, "주문 메시지는 500자 이내로 입력해 주세요.");
        }
    }
}
