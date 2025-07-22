package gift.item;

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
@Table(name = "item")
public class ItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 15)
    private String name;

    @Column(name = "price", nullable = false)
    private Integer price;

    @Column(name = "image_url", nullable = false, length = 255)
    private String imageUrl;

    @OneToMany(
        mappedBy = "item",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<OptionEntity> options = new ArrayList<>();

    protected ItemEntity() {
    }


    // 새로운 item 생성시 사용하는 생성자
    public ItemEntity(String name, Integer price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
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

    public List<OptionEntity> getOptions() {
        return options;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
