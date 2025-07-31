package gift.product.entity;

import gift.product.dto.request.OrderRequest;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Option option;

    @Column(nullable = false)
    private Integer quantity;

    @Column()
    private LocalDateTime orderDateTime;

    @Column(nullable = false, length = 255)
    private String message;

    protected Order() {}

    public Order(Option option, OrderRequest orderRequest) {
        this.option = option;
        this.quantity = orderRequest.quantity();
        this.orderDateTime = LocalDateTime.now();
        this.message = orderRequest.message();
    }

    public Long getId() {
        return id;
    }

    public Option getOption() {
        return option;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }
}
