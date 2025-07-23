package gift.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class ProductRequestDto {

    private Long id;

    @NotNull
    @Size(max = 15, message = "공백 포함 최대 15자까지만 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$", message = "특수문자는 (), [], +, -, &, /, _ 만 가능합니다.")
    private String name;

    @NotNull
    private String imageUrl;

    @NotNull
    private Long price;

    public ProductRequestDto() {}

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
