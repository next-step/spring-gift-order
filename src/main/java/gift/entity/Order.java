package gift.entity;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_option_id", nullable = false)
    private ProductOption productOption;

    @Embedded
    private OrderQuantity quantity;

    @Embedded
    private OrderMessage message;

    protected Order() {
    }

    private Order(Long id, Member member, ProductOption productOption, int quantity,
        String message) {
        this.id = id;
        this.member = member;
        this.productOption = productOption;
        this.quantity = new OrderQuantity(quantity);
        this.message = new OrderMessage(message);
    }

    public Order(Member member, ProductOption productOption, int quantity, String message) {
        this(null, member, productOption, quantity, message);
    }

    public Long getId() {
        return id;
    }

    public Member getMember() {
        return member;
    }

    public ProductOption getProductOption() {
        return productOption;
    }

    public int getQuantity() {
        return quantity.quantity();
    }

    public String getMessage() {
        return message.message();
    }
}
