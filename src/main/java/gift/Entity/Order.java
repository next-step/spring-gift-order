package gift.Entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    private Option option;

    private int quantity;

    private LocalDateTime orderDateTime;

    @Column(length = 500)
    private String message;

    public Order() {}

    public Order(Member member, Option option, int quantity, String message) {
        this.member = member;
        this.option = option;
        this.quantity = quantity;
        this.message = message;
        this.orderDateTime = LocalDateTime.now();
    }

    // Getter
    public Long getId() { return id; }
    public Member getMember() { return member; }
    public Option getOption() { return option; }
    public int getQuantity() { return quantity; }
    public LocalDateTime getOrderDateTime() { return orderDateTime; }
    public String getMessage() { return message; }
}

