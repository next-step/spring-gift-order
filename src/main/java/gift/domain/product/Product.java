package gift.domain.product;

import gift.common.exception.DuplicateOptionNameException;
import gift.common.exception.ProductOptionException;
import gift.dto.product.CreateProductOptionRequest;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 15)
    private String name;

    @Column(nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    public Product(String name, String imageUrl, List<CreateProductOptionRequest> options) {
        addOption(options.toArray(new CreateProductOptionRequest[0]));
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public List<ProductOption> getOptions() {
        return options;
    }

    public void update(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public void addOption(CreateProductOptionRequest... request) {
        for (CreateProductOptionRequest r : request) {
            if (existOptionName(r.name())) {
                throw new DuplicateOptionNameException();
            }
            ProductOption option = ProductOption.of(this, r.name(), r.price(), r.quantity());
            options.add(option);
        }
    }

    public void removeOption(ProductOption option) {
        if (options.size() < 2) {
            throw new ProductOptionException();
        }
        options.remove(option);
    }

    private boolean existOptionName(String name) { // N+1문제
        return options.stream().anyMatch(op -> op.getName().equals(name));
    }

    protected Product() {
    }
}
