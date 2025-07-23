package gift.entity;

import gift.domain.product.MdApprovalStatus;
import gift.domain.product.ProductName;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private ProductName name;

    private Long price;

    private String imageUrl;

    @Embedded
    @AttributeOverride(name = "approved", column = @Column(name = "md_approved"))
    private MdApprovalStatus mdApproval;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {
    }

    public Product(String name, Long price, String imageUrl) {
        this.name = new ProductName(name);
        this.price = price;
        this.imageUrl = imageUrl;
        this.mdApproval = MdApprovalStatus.of(name);
    }

    public Product(String name, long price, String imageUrl, MdApprovalStatus approved) {
        this.name = new ProductName(name);
        this.price = price;
        this.imageUrl = imageUrl;
        this.mdApproval = approved;
    }

    public void update(String name, Long price, String imageUrl) {
        this.name = new ProductName(name);
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name.getValue();
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public boolean isApproved() {
        return mdApproval != null && mdApproval.isApproved();
    }

    public void setId(Long productId) {
        this.id = productId;
    }

    public void addOption(ProductOption option) {
        options.add(option);
        option.setProduct(this);
    }

    public void validateOptions() {
        if (options == null || options.isEmpty()) {
            throw new IllegalStateException("상품에는 최소 한 개 이상의 옵션이 필요합니다.");
        }
    }

    public List<ProductOption> getOptions() { return options; }
    public void setOptions(List<ProductOption> options) { this.options = options; }
}