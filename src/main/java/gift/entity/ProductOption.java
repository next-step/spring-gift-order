package gift.entity;

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
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(
    name = "product_option",
    uniqueConstraints = @UniqueConstraint(
        name = "unique_name_productId",
        columnNames = {"name", "product_id"}
    )
)
public class ProductOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(
        regexp = "^$|^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]+$",
        message = "허용되지 않는 특수문자가 포함되어 있습니다."
    )
    @Column(name = "name", nullable = false)
    private String name;

    @Min(value = 1, message = "1 이상 1억 미만의 값을 가져야 합니다.")
    @Max(value = 99999999, message = "1 이상 1억 미만의 값을 가져야 합니다.")
    @Column(name = "quantity", nullable = false)
    private int quantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    protected ProductOption() {
    }

    public ProductOption(Long id, String name, int quantity, Product product) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.product = product;
    }

    public ProductOption(String name, int quantity, Product product) {
        this(null, name, quantity, product);
    }

    public ProductOption(String name, int quantity) {
        this(null, name, quantity, null);
    }

    public void change(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;

        if (product != null) {
            this.product.recalculateQuantity();
        }
    }

    public ProductOption withProduct(Product product) {
        return new ProductOption(null, this.name, this.quantity, product);
    }

    public void changeQuantity(int quantity) {
        this.quantity = quantity;

        if (product != null) {
            this.product.recalculateQuantity();
        }
    }
}
