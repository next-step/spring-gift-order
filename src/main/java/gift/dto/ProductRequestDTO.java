package gift.dto;

import gift.validation.DisallowKakao;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.util.List;

public class ProductRequestDTO {

    @Size(max = 15, message = "상품 이름은 공백을 포함하여 최대 15자까지 입력할 수 있습니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎ\\s()\\[\\]+\\-&/_]*$", message = "허용되지 않는 특수문자가 포함되어 있습니다. 사용 가능한 특수문자: ( ), [ ], +, -, &, /, _")
    @DisallowKakao
    private String name;

    @NotNull(message = "상품 가격은 필수입니다.")
    @Positive(message = "상품 가격은 0보다 큰 값이어야 합니다.")
    private Long price;

    @Pattern(regexp = "^(https?://)([\\w-]+\\.)+[\\w-]+(/[\\w-./?%&=]*)?$", message = "올바른 URL 형식이 아닙니다.")
    private String imageUrl;

    @Valid
    @NotNull(message = "상품 옵션은 필수입니다.")
    @NotEmpty(message = "상품에는 최소 하나 이상의 옵션이 있어야 합니다.")
    private List<OptionRequestDTO> options;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<OptionRequestDTO> getOptions() {
        return options;
    }
}
