package gift.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import gift.domain.Order;
import java.time.LocalDateTime;

public class OrderResponse {
    private Long id;
    private Long optionId;
    private int quantity;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDateTime;
    private String message;

    public OrderResponse(Long id, Long optionId, int quantity, LocalDateTime orderDateTime, String message) {
        this.id = id;
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
    }

    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getOption().getId(),
            order.getQuantity(),
            order.getCreatedAt(),
            order.getMessage()
        );
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

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }
}
