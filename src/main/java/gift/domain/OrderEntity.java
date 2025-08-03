package gift.domain;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class OrderEntity {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private ProductOption option;

    private int quantity;
    private String message;
    private LocalDateTime orderDateTime = LocalDateTime.now();

    public OrderEntity(ProductOption option, int quantity, String message) {
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public ProductOption getOption() { return option; }
    public void setOption(ProductOption option) { this.option = option; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getOrderDateTime() { return orderDateTime; }
    public void setOrderDateTime(LocalDateTime orderDateTime) { this.orderDateTime = orderDateTime; }
}
