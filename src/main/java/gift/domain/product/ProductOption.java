package gift.domain.product;

import gift.common.exception.NotEnoughQuantityException;
import jakarta.persistence.*;

import static jakarta.persistence.FetchType.*;

@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(nullable = false)
    private Product product;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private Integer quantity;

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    static ProductOption of(Product product, String name, Integer price, Integer quantity) {
        return new ProductOption(product, name, price, quantity);
    }

    public void subtractQuantity(int quantity) {
        Integer q = this.quantity;
        if (q - quantity < 0) {
            throw new NotEnoughQuantityException(this.quantity);
        }
        this.quantity -= quantity;
    }

    private ProductOption(Product product, String name, Integer price, Integer quantity) {
        this.product = product;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    protected ProductOption() {
    }
}
