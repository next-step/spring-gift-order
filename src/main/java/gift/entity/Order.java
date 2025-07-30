package gift.entity;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "option_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_option_id_ref_option_id")
    )
    private Option option;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private Long price;

    @Column(name = "order_date_time", nullable = false)
    private Instant orderDateTime;

    @Column(nullable = false)
    private String message;

    protected Order() {
    }

    public Order(Option option, Integer quantity, Long price, Instant orderDateTime, String message) {
        this.option = option;
        this.quantity = quantity;
        this.price = price;
        this.orderDateTime = orderDateTime;
        this.message = message;
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

    public Long getPrice() {
        return price;
    }

    public Instant getOrderDateTime() {
        return orderDateTime;
    }

    public String getMessage() {
        return message;
    }
}
