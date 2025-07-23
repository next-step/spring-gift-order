package gift.entity;

import gift.dto.ProductRequestDTO;
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

    @Column(length = 15, nullable = false)
    private String name;

    @Column(nullable = false)
    private Long price;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Option> options = new ArrayList<>();

    public Product() {
    }

    public Product(String name, Long price, String imageUrl) {
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public void updateFromProductRequestDTO(ProductRequestDTO dto) {
        this.name = dto.getName();
        this.price = dto.getPrice();
        this.imageUrl = dto.getImageUrl();

        this.options.clear();
        for (var optionDTO : dto.getOptions()) {
            Option option = new Option(optionDTO.name(), optionDTO.quantity(), this);
            this.options.add(option);
        }
        validateHasOptions();
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void addOption(Option option) {
        if (hasOptionWithName(option.getName())) {
            throw new IllegalArgumentException("동일한 상품 내에서 옵션 이름이 중복될 수 없습니다.");
        }
        this.options.add(option);
        option.setProduct(this);
    }

    public void removeOption(Option option) {
        if (this.options.size() <= 1) {
            throw new IllegalStateException("상품에는 최소 하나 이상의 옵션이 있어야 합니다.");
        }
        this.options.remove(option);
        option.setProduct(null);
    }

    public boolean hasOptionWithName(String name) {
        return this.options.stream()
            .anyMatch(option -> option.getName().equals(name));
    }

    public Option getOptionById(Long optionId) {
        return this.options.stream()
            .filter(option -> option.getId().equals(optionId))
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("해당 상품에서 옵션을 찾을 수 없습니다."));
    }

    public List<Option> getOptions() {
        return options;
    }

    private void validateHasOptions() {
        if (this.options.isEmpty()) {
            throw new IllegalStateException("상품에는 최소 하나 이상의 옵션이 있어야 합니다.");
        }
    }
}