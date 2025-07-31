package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_id", nullable = false)
    private Option option;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, updatable = false)
    private ZonedDateTime orderDateTime;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;


    protected Order() {
    }

    public Order(Member member, Option option, int quantity, String message) {
        this.member = member;
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = ZonedDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public Option getOption() {
        return option;
    }

    public int getQuantity() {
        return quantity;
    }

    public ZonedDateTime getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }
}