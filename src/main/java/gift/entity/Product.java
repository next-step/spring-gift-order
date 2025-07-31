package gift.entity;

import jakarta.persistence.*;
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
    private Long price;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    protected Product() {
    }

    public Product(String name, Long price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    // --- Getter 메소드 ---
    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public List<Option> getOptions() { return options; }

    // --- Setter 메소드 ---
    public void setId(Long id) { // ✅ 이 메소드가 필요합니다!
        this.id = id;
    }
    public void setName(String name) { this.name = name; }
    public void setPrice(Long price) { this.price = price; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // --- 편의 메소드 ---
    public void addOption(Option option) {
        this.options.add(option);
        option.setProduct(this);
    }
}