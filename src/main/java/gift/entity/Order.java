package gift.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(name = "member_id", nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    @Column(columnDefinition = "TEXT")
    private String message;

    protected Order() {
    }

    public Order(Long optionId, Long memberId, int quantity, String message) {
        this.optionId = optionId;
        this.memberId = memberId;
        this.quantity = quantity;
        this.message = message;
    }

    @PrePersist
    public void prePersist() {
        this.orderDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Long getOptionId() {
        return optionId;
    }

    public Long getMemberId() {
        return memberId;
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

