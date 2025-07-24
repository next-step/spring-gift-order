package gift.dto;

import gift.validation.ForbiddenWordKakao;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public class ProductRequest {

    private Long id;

    @NotBlank(message = "상품 이름은 필수입니다.")
    @Size(max = 15, message = "상품 이름은 최대 15자까지 허용됩니다.")
    @Pattern(
            regexp = "^[A-Za-z0-9\\s()\\[\\]\\+\\-&/_]*$",
            message = "특수문자는 () [] + - & / _ 만 허용됩니다."
    )
    @ForbiddenWordKakao // “카카오” 단어 포함 금지
    private String name;

    @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
    private int price;

    @NotBlank(message = "이미지 URL은 필수입니다.")
    @URL(message = "유효한 URL 형식이어야 합니다.")
    private String imageUrl;

    public ProductRequest() {
    }

    public ProductRequest(Long id, String name, int price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}