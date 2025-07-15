package gift.product;

import gift.product.exception.InvalidProductOptionException;
import gift.product.exception.NotEnoughInventoryException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;

@Entity
public class ProductOption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
    @Column(length = 50, nullable = false)
    private String name;
    private Long quantity;

    protected ProductOption() {
    }

    public ProductOption(String name, Long quantity) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validateName(String name) {
        if (name == null || name.length() > 50) {
            throw new InvalidProductOptionException("optionError","옵션 이름은 50자 이하여야 합니다.");
        }

        if (!name.matches(ValidationPatterns.ALLOWED_NAME_PATTERN)) {
            throw new InvalidProductOptionException("optionError","옵션 이름에는 영어, 한글, 숫자와 특수문자 (), [], +, -, &, /, _ 만 사용할 수 있습니다.");
        }
    }

    private void validateQuantity(Long quantity) {
        if (quantity == null || quantity < 1 || quantity >= 100_000_000L) {
            throw new InvalidProductOptionException("optionError","옵션 수량은 1개 이상 1억 미만이어야 합니다.");
        }
    }

    protected void decreaseQuantity(Long amount) {
        if (amount == null || amount <= 0) {
            throw new InvalidProductOptionException("optionError","수량이 잘못 입력되었습니다.");
        }

        if (this.quantity < amount) {
            throw new NotEnoughInventoryException(this.quantity, amount);
        }

        this.quantity -= amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
