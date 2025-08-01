package gift.service;

import org.springframework.context.ApplicationEvent;

public class OrderCompletedEvent extends ApplicationEvent {

    private final Long orderId;
    private final Integer quantity;
    private final String message;
    private final String accessToken;

    public OrderCompletedEvent(Object source, Long orderId, Integer quantity, String message,
        String accessToken) {
        super(source);
        this.orderId = orderId;
        this.quantity = quantity;
        this.message = message;
        this.accessToken = accessToken;
    }

    public Long getOrderId() {
        return orderId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public String getAccessToken() {
        return accessToken;
    }
}
