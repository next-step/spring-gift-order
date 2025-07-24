package gift.domain;

import jakarta.persistence.*;

@Entity
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;

    @Column(length = 50, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long quantity;

    protected ProductOption() {}

    public ProductOption(String name, Long quantity) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
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

    public Long getQuantity() {
        return quantity;
    }

    public void assignToProduct(Product product) {
        this.product = product;
    }

    public void subtractQuantity(long quantityToSubtract) {
        if (quantityToSubtract <= 0) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }
        if (this.quantity < quantityToSubtract) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= quantityToSubtract;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다.");
        }
        if (name.length() > 50) {
            throw new IllegalArgumentException("옵션 이름은 최대 50자까지 가능합니다.");
        }
        if (!name.matches("^[가-힣a-zA-Z0-9\\s()\\[\\]\\+\\-&/_]+$")) {
            throw new IllegalArgumentException("허용되지 않은 문자가 포함되었습니다.");
        }
    }

    private void validateQuantity(Long quantity) {
        if (quantity == null || quantity < 1 || quantity >= 100_000_000) {
            throw new IllegalArgumentException("수량은 1 이상 1억 미만이어야 합니다.");
        }
    }
}
