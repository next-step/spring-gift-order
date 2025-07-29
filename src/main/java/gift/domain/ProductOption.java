package gift.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

import java.util.regex.Pattern;

@Entity
public class ProductOption {
    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣 ()\\[\\]+\\-\\&/_]+$");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    @JsonIgnore
    private Product product;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private int quantity;

    protected ProductOption() {
    }

    public ProductOption(Product product, String name, int quantity) {
        this.product = product;
        validateName(name);
        validateDuplicateName(product, name);
        this.name = name;
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    public void update(String name, int quantity) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    private void validateName(String name) {
        if (name == null || name.length() > 50 || !VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("옵션 이름은 50자 이내의 허용된 문자로 작성해야 합니다.");
        }
    }

    public void validateQuantity(int quantity) {
        if (quantity < 1 || quantity >= 100_000_000) {
            throw new IllegalArgumentException("옵션 수량은 1 이상 1억 미만이어야 합니다.");
        }
    }

    public boolean isDuplicateName(Product product, String newName) {
        return product.getOptions().stream()
                .anyMatch(option -> option.getName().equals(newName));
    }

    public void validateDuplicateName(Product product, String newName) {
        if (isDuplicateName(product, newName)) {
            throw new IllegalArgumentException("해당 상품에 동일한 옵션 이름이 존재합니다.");
        }
    }

    public void subtract(int amount) {
        if (amount < 1 || amount > this.quantity) {
            throw new IllegalArgumentException("삭제할 수량이 잘못되었습니다.");
        }
        this.quantity -= amount;
    }

    public void decreaseQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }

        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.quantity);
        }

        this.quantity -= amount;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

}
