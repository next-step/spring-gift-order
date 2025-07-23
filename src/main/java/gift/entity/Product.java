package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @Column(name = "price", nullable = false)
    private Long price;

    public Product() {
    }

    public static Product of(String name, String imageUrl, Long price) {
        return new Product(null, name, imageUrl, price);
    }

    private Product(Long id, String name, String imageUrl, Long price) {
        this.id = id;
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Long getPrice() {
        return price;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void update(String name, String imageUrl, Long price) {
        if(name == null) {
            throw new IllegalArgumentException("상품 이름은 필수입니다.");
        }

        if(price == null || price <= 0) {
            throw new IllegalArgumentException("상품 가격은 0보다 커야 합니다.");
        }

        if(imageUrl == null) {
            throw new IllegalArgumentException("이미지 URL은 필수입니다.");
        }

        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
    }
}
