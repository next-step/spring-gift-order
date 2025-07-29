package gift.entity;

import gift.common.exception.core.CustomException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import org.springframework.http.HttpStatus;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductName name;

    @Embedded
    private ProductPrice price;

    @Embedded
    private ProductImageUrl imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    private Product(Long id, String name, Integer price, String imageUrl) {
        this.id = id;
        this.name = new ProductName(name);
        this.price = new ProductPrice(price);
        this.imageUrl = new ProductImageUrl(imageUrl);
    }

    public Product(String name, Integer price, String imageUrl) {
        this(null, name, price, imageUrl);
    }

    public void addOption(ProductOption option) {
        options.add(option);
        option.setProduct(this);
    }

    public ProductOption addUniqueOption(String name, Long quantity) {
        validateDuplicateOptionName(name);
        ProductOption option = ProductOption.of(name, quantity, this);
        this.addOption(option);
        return option;
    }

    public void update(String name, Integer price, String imageUrl) {
        this.name = new ProductName(name);
        this.price = new ProductPrice(price);
        this.imageUrl = new ProductImageUrl(imageUrl);
    }

    private void validateDuplicateOptionName(String name) {
        boolean isOptionNameDuplicated = options.stream()
            .anyMatch(opt -> opt.getName().equals(name));
        if (isOptionNameDuplicated) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "동일한 상품 내 옵션 이름은 중복될 수 없습니다.");
        }
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.name();
    }

    public Integer getPrice() {
        return price.price();
    }

    public String getImageUrl() {
        return imageUrl.imageUrl();
    }
}
