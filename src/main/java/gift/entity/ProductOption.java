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
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "product_option", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"product_id", "name"})
})
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductOptionName name;

    @Embedded
    private ProductOptionQuantity quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected ProductOption() {
    }

    private ProductOption(String name, Long quantity, Product product) {
        this.name = new ProductOptionName(name);
        this.quantity = new ProductOptionQuantity(quantity);
        this.product = product;
    }

    public static ProductOption of(String name, Long quantity, Product product) {
        return new ProductOption(name, quantity, product);
    }

    public void update(String name, Long quantity) {
        this.name = new ProductOptionName(name);
        this.quantity = new ProductOptionQuantity(quantity);
    }

    public void decreaseQuantity(long amount) {
        this.quantity = quantity.decrease(amount);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.name();
    }

    public Long getQuantity() {
        return quantity.quantity();
    }

    public Product getProduct() {
        return product;
    }

    protected void setProduct(Product product) {
        this.product = product;
    }
}
