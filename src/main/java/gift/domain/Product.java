package gift.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.util.List;
import org.springframework.util.Assert;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private int price;
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options;

    protected Product() {
    }

    public Product(String name, int price, String imageUrl, List<Option> options) {
        Assert.hasText(name, "상품 이름은 비어 있을 수 없습니다.");
        Assert.isTrue(price >= 0, "상품 가격은 0 이상이어야 합니다.");
        Assert.notEmpty(options, "상품에는 하나 이상의 옵션이 있어야 합니다.");
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options = options;
        options.forEach(option -> option.setProduct(this));
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public List<Option> getOptions() {
        return options;
    }

    public void update(String name, int price, String imageUrl, List<Option> options) {
        Assert.hasText(name, "상품 이름은 비어 있을 수 없습니다.");
        Assert.isTrue(price >= 0, "상품 가격은 0 이상이어야 합니다.");
        Assert.notEmpty(options, "상품에는 하나 이상의 옵션이 있어야 합니다.");
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.options.clear();
        this.options.addAll(options);
        options.forEach(option -> option.setProduct(this));
    }
}
