package gift.entity;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import jakarta.persistence.Column;
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

    private static final int MAX_NAME_LENGTH = 50;
    private static final String NAME_PATTERN = "^[\\p{L}0-9()\\[\\]+\\-&/_ ]+$";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long quantity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected ProductOption() {
    }

    public ProductOption(String name, Long quantity, Product product) {
        validateName(name);
        validateQuantity(quantity);

        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public static ProductOption of(String name, Long quantity, Product product) {
        return new ProductOption(name, quantity, product);
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new CustomException(CustomResponseCode.OPTION_NAME_REQUIRED);
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new CustomException(CustomResponseCode.OPTION_NAME_TOO_LONG);
        }
        if (!name.matches(NAME_PATTERN)) {
            throw new CustomException(CustomResponseCode.OPTION_NAME_INVALID_CHAR);
        }
    }

    private void validateQuantity(Long quantity) {
        if (quantity == null || quantity < 1 || quantity >= 100_000_000L) {
            throw new CustomException(CustomResponseCode.OPTION_QUANTITY_INVALID);
        }
    }

    public void decreaseQuantity(long amount) {
        if (amount < 1) {
            throw new CustomException(CustomResponseCode.OPTION_DECREASE_AMOUNT_INVALID);
        }
        if (amount > this.quantity) {
            throw new CustomException(CustomResponseCode.OPTION_INSUFFICIENT_STOCK);
        }
        this.quantity -= amount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public Product getProduct() {
        return product;
    }

    protected void setProduct(Product product) {
        this.product = product;
    }
}
