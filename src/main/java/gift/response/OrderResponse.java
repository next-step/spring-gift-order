package gift.response;

import gift.Entity.Order;

import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private Long optionId;
    private int quantity;
    private LocalDateTime orderDateTime;
    private String message;

    public OrderResponse(Order order) {
        this.id = order.getId();
        this.optionId = order.getOption().getId();
        this.quantity = order.getQuantity();
        this.orderDateTime = order.getOrderDateTime();
        this.message = order.getMessage();
    }

    public Long getId() { return id; }
    public Long getOptionId() { return optionId; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getOrderDateTime() { return orderDateTime; }
    public String getMessage() { return message; }
}

