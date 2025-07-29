package gift.order.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Message {

    @Column(name = "message", nullable = false)
    private String message;

    protected Message() {
    }

    public Message(String message) {
        if (message == null) {
            throw new IllegalArgumentException("Message는 null일 수 없습니다.");
        }

        if (message.length() > 100) {
            throw new IllegalArgumentException("Message 길이는 100이하이어야 합니다.");
        }

        this.message = message;
    }

    public String toValue() {
        return message;
    }

}
