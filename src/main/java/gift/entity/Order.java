package gift.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long memberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_options_id")
    private ProductOption productOption;

    private Integer quantity;
    @Column(name = "order_date_time", nullable = false, updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime orderDateTime;
    private String message;

    @PrePersist
    protected void onCreate() {
        this.orderDateTime = LocalDateTime.now().withNano(0);
    }

    public Order(Long memberId, ProductOption productOption,Integer quantity, String message) {
        this.memberId = memberId;
        this.productOption = productOption;
        this.quantity = quantity;
        this.message = message;
    }

    public Order() {
    }

    public Long getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public ProductOption getProductOption() {
        return productOption;
    }
}
