package gift.dto;

import gift.domain.ProductOption;
import gift.validation.NoKakao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.util.List;
public class ProductRequest {
    private Long id;

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(max = 15, message = "상품 이름은 최대 15자까지 입력 가능합니다.")
    @Pattern(
            regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$",
            message = "상품 이름에는 허용되지 않는 특수 문자가 포함되어 있습니다."
    )
    @NoKakao
    private String name;

    @Positive(message = "상품 가격은 0보다 커야 합니다.")
    private int price;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    private String imageUrl;

    private List<ProductOptionResponse> options;

    public ProductRequest(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public ProductRequest() {
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<ProductOptionResponse> getOptions() {
        return options;
    }

    public void setOptions(List<ProductOptionResponse> options) {
        this.options = options;
    }
}
