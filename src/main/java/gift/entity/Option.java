package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.regex.Pattern;

@Entity
public class Option {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false)
    private int quantity;

    @ManyToOne
    private Product product;

    protected Option() {}

    public static Option of(String name, int quantity, Product product) {
        return new Option(null, name, quantity, product);
    }

    private Option(Long id, String name, int quantity, Product product) {
        validateName(name);
        validateQuantity(quantity);
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    static final Pattern SPECIAL_LETTERS = Pattern.compile("^[()\\[\\]+\\-&_\\/a-zA-Z0-9가-힣 ]+$");

    private static void validateName(String name) {
        if(name.length() > 50) {
            throw new IllegalArgumentException("옵션 이름은 공백 포함 50자를 넘을 수 없습니다.");
        }

        if(!SPECIAL_LETTERS.matcher(name).matches()) {
            throw new IllegalArgumentException("옵션 이름에 특수문자는 ( ), [ ], +, -, &, /, _만 사용 가능합니다.");
        }

    }

    private static void  validateQuantity(int quantity) {
        if (quantity <= 0 || quantity > 100_000_000) {
            throw new IllegalArgumentException("옵션 수량은 최소 1개 이상, 1억 개 미만입니다.");
        }
    }

    public Long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public int getQuantity() {
        return quantity;
    }
    public Product getProduct() {
        return product;
    }

    public void subtract(int count) {
        if(count < 1) {
            throw new IllegalArgumentException("차감 수량은 1 이상이어야 합니다.");
        }

        if(this.quantity < count) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        this.quantity -= count;
    }
}
