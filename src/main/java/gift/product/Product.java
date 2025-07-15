package gift.product;

import gift.product.dto.ProductResponseDto;
import gift.product.dto.ProductUpdateRequestDto;
import gift.product.exception.InvalidProductOptionException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Product {
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ProductOption> options = new ArrayList<>();
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Long price;
    private String url;

    protected Product() {
    }

    public Product(String name, Long price, String url) {
        this.name = name;
        this.price = price;
        this.url = url;
    }

    public ProductResponseDto toProductResponseDto() {
        return new ProductResponseDto(this);
    }

    public void update(ProductUpdateRequestDto requestDto) {
        this.name = requestDto.name();
        this.price = requestDto.price();
        this.url = requestDto.url();
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

    public String getUrl() {
        return url;
    }

    public List<ProductOption> getOptions() {
        return Collections.unmodifiableList(options);
    }

    public void setOptions(List<ProductOption> newOptions) {
        if (newOptions == null || newOptions.isEmpty()) {
            throw new InvalidProductOptionException("optionError","상품에는 최소 하나 이상의 옵션이 있어야 합니다.");
        }

        Set<String> optionNames = new HashSet<>();
        for (ProductOption option : newOptions) {
            if (!optionNames.add(option.getName())) {
                throw new InvalidProductOptionException("optionNameError","옵션 이름이 중복됩니다: " + option.getName());
            }
            option.setProduct(this);
        }

        this.options.clear();
        this.options.addAll(newOptions);
    }
}