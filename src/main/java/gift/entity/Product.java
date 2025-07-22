package gift.entity;

import gift.dto.ProductRequestDto;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Long price;
    private String imageUrl;

    @OneToMany(mappedBy = "product")
    private List<ProductOption> options = new ArrayList<>();

    protected Product() {}

    public Product(String name, Long price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Product(ProductRequestDto productRequestDto) {
        this.name = productRequestDto.name();
        this.price = productRequestDto.price();
        this.imageUrl = productRequestDto.imageUrl();
    }

    public void update(String name, Long price, String imageUrl){
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public Long getPrice() { return price; }
    public String getImageUrl() { return imageUrl; }
    public List<ProductOption> getOptions() { return options; }
}
