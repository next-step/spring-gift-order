package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9 ()\\[\\]+\\-&/_]{1,15}$",
        message = "15자 이내의 한글, 영문, (), [], +, -, &, /, _만 사용할 수 있습니다")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "가격은 1원 이상이어야 합니다")
    @Column(nullable = false)
    private Integer price;

    @URL(message = "URL형식만 입력할 수 있습니다")
    @Column(nullable = false)
    private String imageURL;

    public Long getId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}