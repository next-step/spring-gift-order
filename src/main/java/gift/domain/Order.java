package gift.domain;

import gift.domain.user.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private Integer pricePerUnit;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private LocalDateTime orderDateTime;

    protected Order() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public Long getOptionId() {
        return optionId;
    }

    public Order(User user, Long optionId, Integer pricePerUnit, Integer quantity) {
        this.user = user;
        this.optionId = optionId;
        this.pricePerUnit = pricePerUnit;
        this.quantity = quantity;
        this.orderDateTime = LocalDateTime.now();
    }
}
