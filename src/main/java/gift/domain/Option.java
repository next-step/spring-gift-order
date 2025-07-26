package gift.domain;

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
import java.util.regex.Pattern;

@Entity
@Table(
    name = "product_option",
    uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "name"})
)
public class Option {

    private static final Pattern VALID_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9가-힣\\s\\(\\)\\[\\]\\+\\-&/_\\.]+$");
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MIN_QUANTITY = 1;
    private static final int MAX_QUANTITY = 100_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private Integer quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    protected Option() {
        // JPA 기본 생성자
    }

    public Option(String name, Integer quantity, Product product) {
        validateName(name);
        validateQuantity(quantity);
        validateProduct(product);
        
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public static Option of(String name, Integer quantity, Product product) {
        return new Option(name, quantity, product);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Integer quantity() {
        return quantity;
    }

    public Product product() {
        return product;
    }

    public void subtract(Integer amount) {
        validateSubtractAmount(amount);
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= amount;
    }

    public void updateName(String name) {
        validateName(name);
        this.name = name;
    }

    public void updateQuantity(Integer quantity) {
        validateQuantity(quantity);
        this.quantity = quantity;
    }

    private static void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 비어있을 수 없습니다.");
        }
        if (name.length() > MAX_NAME_LENGTH) {
            throw new IllegalArgumentException("옵션 이름은 최대 " + MAX_NAME_LENGTH + "자까지 입력할 수 있습니다.");
        }
        if (!VALID_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 특수 문자가 포함되어 있습니다. 허용 문자: ( ), [ ], +, -, &, /, _");
        }
    }

    private static void validateQuantity(Integer quantity) {
        if (quantity == null) {
            throw new IllegalArgumentException("옵션 수량은 null일 수 없습니다.");
        }
        if (quantity < MIN_QUANTITY || quantity >= MAX_QUANTITY) {
            throw new IllegalArgumentException("옵션 수량은 " + MIN_QUANTITY + "개 이상 " + MAX_QUANTITY + "개 미만이어야 합니다.");
        }
    }

    private static void validateProduct(Product product) {
        if (product == null) {
            throw new IllegalArgumentException("상품은 null일 수 없습니다.");
        }
    }

    private static void validateSubtractAmount(Integer amount) {
        if (amount == null) {
            throw new IllegalArgumentException("차감할 수량은 null일 수 없습니다.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 수량은 0보다 커야 합니다.");
        }
    }
}
