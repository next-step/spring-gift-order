package gift.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_options",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "name"}))
public class ProductOption {

    private static final int MAX_OPTION_NAME_LENGTH = 50;
    private static final String ALLOWED_OPTION_NAME_PATTERN = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ()\\[\\]+\\-&/_\\s]*$";
    private static final String ALLOWED_CHAR_PATTERN = "[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ()\\[\\]+\\-&/_\\s]";
    private static final int MIN_OPTION_QUANTITY = 1;
    private static final int MAX_OPTION_QUANTITY = 100_000_000;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false, length = MAX_OPTION_NAME_LENGTH)
    private String name;

    @Column(nullable = false)
    private int quantity;

    public ProductOption() {}

    public ProductOption(String name, int quantity) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
    }

    public void subtract(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("차감할 수량은 1 이상이어야 합니다.");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("재고가 부족합니다. 현재 재고: " + this.quantity + ", 요청 수량: " + amount);
        }
        this.quantity -= amount;
    }

    public void add(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("추가할 수량은 1 이상이어야 합니다.");
        }
        this.quantity += amount;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("옵션 이름은 필수입니다.");
        }
        if (name.length() > MAX_OPTION_NAME_LENGTH) {
            throw new IllegalArgumentException("옵션 이름은 " + MAX_OPTION_NAME_LENGTH + "자를 초과할 수 없습니다.");
        }
        if (!isValidName(name)) {
            String invalidChars = name.chars()
                    .mapToObj(c -> (char) c)
                    .filter(c -> !String.valueOf(c).matches(ALLOWED_CHAR_PATTERN))
                    .distinct()
                    .map(String::valueOf)
                    .reduce("", (a, b) -> a + b);
            throw new IllegalArgumentException("옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다: [" + invalidChars + "]");
        }
    }

    private boolean isValidName(String name) {
        return name.matches(ALLOWED_OPTION_NAME_PATTERN);
    }

    private void validateQuantity(int quantity) {
        if (quantity < MIN_OPTION_QUANTITY) {
            throw new IllegalArgumentException("옵션 수량은 최소 "+ MIN_OPTION_QUANTITY + "개 이상이어야 합니다.");
        }
        if (quantity >= MAX_OPTION_QUANTITY) {
            throw new IllegalArgumentException("옵션 수량은 "+MAX_OPTION_QUANTITY+"개 미만이어야 합니다.");
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
