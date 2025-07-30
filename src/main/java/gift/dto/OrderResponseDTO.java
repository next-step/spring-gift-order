package gift.dto;

import gift.entity.Order;
import java.time.LocalDateTime;

public class OrderResponseDTO {

    private Long id;
    private Long productId;
    private Long optionId;
    private int quantity;
    private LocalDateTime orderDateTime;
    private String message;

    public OrderResponseDTO() {
    }

    public OrderResponseDTO(Long id, Long productId, Long optionId, int quantity, LocalDateTime orderDateTime, String message) {
        this.id = id;
        this.productId = productId;
        this.optionId = optionId;
        this.quantity = quantity;
        this.orderDateTime = orderDateTime;
        this.message = message;
    }

    public static OrderResponseDTO from(Order order) {
        return new OrderResponseDTO(
            order.getId(),
            order.getProductId(),
            order.getOptionId(),
            order.getQuantity(),
            order.getOrderDateTime(),
            order.getMessage()
        );
    }

    public Long getId() {
        return id;
    }

    public Long getProductId() {
        return productId;
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
