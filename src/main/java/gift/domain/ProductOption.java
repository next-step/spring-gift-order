package gift.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)

    private Product product;

    protected ProductOption() {
    }

    public ProductOption(String name, int quantity, Product product) {
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public void decreaseQuantity(int amount) {
        if (amount < 1 || amount > this.quantity)
            throw new IllegalArgumentException("감소할 수량이 유효하지 않습니다.");
        this.quantity -= amount;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
}
