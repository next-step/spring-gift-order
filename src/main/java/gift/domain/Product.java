package gift.domain;

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
    
    @Column(nullable = false, length = 15)
    private String name;
    
    @Column(nullable = false)
    private Integer price;
    
    @Column(nullable = false, name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product")
    private List<Wish> wishes = new ArrayList<>();

    protected Product() {
        // JPA 기본 생성자
    }

    public Product(Long id, String name, Integer price, String imageUrl) {
        validateName(name);
        validateImageUrl(imageUrl);
        validatePrice(price);
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public static Product of(String name, Integer price, String imageUrl) {
        return new Product(null, name, price, imageUrl);
    }

    public static Product withId(Long id, String name, Integer price, String imageUrl) {
        validateId(id);
        return new Product(id, name, price, imageUrl);
    }

    public Long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public Integer price() {
        return price;
    }

    public String imageUrl() {
        return imageUrl;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void changeName(String name) {
        validateName(name);
        this.name = name;
    }

    public void changePrice(Integer price) {
        validatePrice(price);
        this.price = price;
    }

    public void changeImageUrl(String imageUrl) {
        validateImageUrl(imageUrl);
        this.imageUrl = imageUrl;
    }

    public void update(String name, Integer price, String imageUrl) {
        requireAtLeastOneFieldChanged(name, price, imageUrl);

        if (isNotBlank(name)) {
            this.changeName(name);
        }
        if (isValidPrice(price)) {
            this.changePrice(price);
        }
        if (isNotBlank(imageUrl)) {
            this.changeImageUrl(imageUrl);
        }
    }

    private static boolean isNotBlank(String value) {
        return value != null && !value.isBlank();
    }

    private static boolean isValidPrice(Integer price) {
        return price != null && price >= 0;
    }

    private static boolean isAllFieldsBlankOrInvalid(String name, Integer price, String imageUrl) {
        return !isNotBlank(name) && !isValidPrice(price) && !isNotBlank(imageUrl);
    }

    private static void requireAtLeastOneFieldChanged(String name, Integer price, String imageUrl) {
        if (isAllFieldsBlankOrInvalid(name, price, imageUrl)) {
            throw new IllegalArgumentException("최소 하나 이상의 필드를 변경해야 합니다.");
        }
    }

    private static void validateId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID는 null일 수 없습니다.");
        }
        if (id < 0) {
            throw new IllegalArgumentException("ID는 음수일 수 없습니다.");
        }
    }

    private static void validateName(String name) {
        if (!isNotBlank(name)) {
            throw new IllegalArgumentException("name은 비어있거나 null일 수 없습니다.");
        }
    }

    private static void validateImageUrl(String imageUrl) {
        if (!isNotBlank(imageUrl)) {
            throw new IllegalArgumentException("imageUrl은 비어있거나 null일 수 없습니다.");
        }
    }

    private static void validatePrice(Integer price) {
        if (!isValidPrice(price)) {
            throw new IllegalArgumentException("가격은 null이거나 음수일 수 없습니다.");
        }
    }
}

