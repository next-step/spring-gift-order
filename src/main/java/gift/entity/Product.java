package gift.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

/* TODO: Product annotation 검증 연동 */

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(max = 15, message = "상품 이름은 공백 포함 최대 15자까지 입력할 수 있습니다.")
    @Pattern(
        regexp = "^$|^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]+$",
        message = "허용되지 않는 특수문자가 포함되어 있습니다."
    )
    @Pattern(
        regexp = "^((?!카카오).)*$",
        message = "'카카오'가 포함된 문구는 담당 MD와 협의한 경우에만 사용할 수 있습니다."
    )
    @Column(name = "name", nullable = false)
    private String name;

    @PositiveOrZero(message = "0 이상 값을 가져야 합니다.")
    @Column(name = "price", nullable = false)
    private int price;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    @Column(name = "imageUrl", nullable = false)
    private String imageUrl;

    @OneToMany(
        mappedBy = "product",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true
    )
    private List<Wish> wishes = new ArrayList<>();

    @OneToMany(
        mappedBy = "product",
        cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
        orphanRemoval = true
    )
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    public Product(Long id, String name, int price, String imageUrl, List<ProductOption> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("상품에 하나 이상의 옵션이 필요합니다.");
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = 0;
        this.imageUrl = imageUrl;
        this.options = new ArrayList<>();

        options.forEach(this::addOption);
    }

    public Product(String name, int price, String imageUrl, List<ProductOption> options) {
        this(null, name, price, imageUrl, options);
    }

    public Long getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getPrice() {
        return this.price;
    }

    public int getQuantity() {
        return this.quantity;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    @PrePersist
    @PreUpdate
    public void validationOptions() {
        if (options.isEmpty()) {
            throw new IllegalArgumentException("상품에 하나 이상의 옵션이 필요합니다.");
        }
    }

    public void change(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Wish addWish(Wish wish) {
        wishes.add(wish);
        wish.setProduct(this);
        return wish;
    }

    public void removeWish(Wish wish) {
        wishes.remove(wish);
        wish.setProduct(null);
    }

    public void addOption(ProductOption option) {
        ProductOption linkedOption = option.withProduct(this);
        options.add(linkedOption);
        recalculateQuantity();
    }

    public void removeOption(ProductOption option) {
        if (options.size() <= 1) {
            throw new IllegalArgumentException("상품에 하나 이상의 옵션이 필요합니다.");
        }

        if (options.remove(option)) {
            recalculateQuantity();
        }
    }

    public void recalculateQuantity() {
        this.quantity = this.options.stream()
            .mapToInt(ProductOption::getQuantity)
            .sum();
    }
}
