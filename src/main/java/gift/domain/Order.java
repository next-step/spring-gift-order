package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(name = "option_id", nullable = false)
    private Long optionId;

    @Column(nullable = false)
    private int quantity;

    @Column(length = 1000)
    private String message;

    @Column(name = "order_date_time", nullable = false)
    private LocalDateTime orderDateTime;

    public Order() {
    }

    public Order(Member member, Long optionId, int quantity, String message) {
        this.member = member;
        this.optionId = optionId;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Long getOptionId() {
        return optionId;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getOrderDateTime() {
        return orderDateTime;
    }
}

