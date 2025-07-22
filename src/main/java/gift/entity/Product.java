package gift.entity;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer price;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    public Product(Long id, String name, Integer price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
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

    private void validateDuplicateOptionName(String name) {
        boolean isOptionNameDuplicated = options.stream()
            .anyMatch(opt -> opt.getName().equals(name));
        if (isOptionNameDuplicated) {
            throw new CustomException(CustomResponseCode.OPTION_DUPLICATED);
        }
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
