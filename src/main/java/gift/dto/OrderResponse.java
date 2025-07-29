package gift.dto;

import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private Long optionId;
    private int quantity;
    private LocalDateTime orderDate;
    private String message;

    public OrderResponse(Long id, Long optionId, int quantity, LocalDateTime orderDate, String message) {
        this.id = id;
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDate = orderDate;
        this.message = message;
    }

    public Long getId() {
        return id;
    }

    public Long getOptionId() {
        return optionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }

    public String getMessage() {
        return message;
    }
}
