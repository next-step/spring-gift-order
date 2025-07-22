package gift.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String name;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductOption> options = new ArrayList<>();

    @Column(nullable = false)
    private int price;

    @Column(name = "image_url", length = 2048)
    private String imageUrl;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public Product() {
    }

    public Product(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(Long id) { this.id = id; }

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

    public List<ProductOption> getOptions() {
        return options;
    }

    public void setId(Long id){
        this.id = id;
    }

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void update(String name, int price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void addOption(ProductOption option) {
        if (isDuplicateOptionName(option.getName())) {
            throw new IllegalArgumentException("중복된 옵션 이름입니다.");
        }
        option.assignToProduct(this);
        options.add(option);
    }

    private boolean isDuplicateOptionName(String name) {
        return options.stream()
                .anyMatch(o -> o.getName().equals(name));
    }

    public void validateAtLeastOneOption() {
        if (options.isEmpty()) {
            throw new IllegalStateException("상품에는 최소 하나 이상의 옵션이 존재해야 합니다.");
        }
    }

}