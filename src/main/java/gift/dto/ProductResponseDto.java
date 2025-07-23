package gift.dto;

import gift.entity.Product;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductResponseDto {

    private final Long id;

    @NotNull
    @Size(max = 15, message = "공백 포함 최대 15자까지만 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$", message = "특수문자는 (), [], +, -, &, /, _ 만 가능합니다.")
    private final  String name;

    @NotNull
    private final String imageUrl;

    @NotNull
    private final Long price;

    public ProductResponseDto(Product product) {
        this.id = product.getId();
        this.name = product.getName();
        this.imageUrl = product.getImageUrl();
        this.price = product.getPrice();
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
}
