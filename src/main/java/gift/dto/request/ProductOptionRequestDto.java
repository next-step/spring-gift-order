package gift.dto.request;

import jakarta.validation.constraints.*;

public class ProductOptionRequestDto {
    @NotBlank(message = "옵션 이름은 필수 입니다")
    @Size(max = 50, message = "옵션 이름은 최대 50자까지 가능합니다.")
    @Pattern(
            regexp = "^[\\p{L}\\p{N}\\s\\(\\)\\[\\]\\+\\-\\&\\/\\_]*$",
            message = "특수문자는 ( ), [ ], +, -, &, /, _ 만 가능합니다."
    )
    private String name;
    @Min(value = 1, message = "옵션 수량은 1개 이상이어야 합니다.")
    @Max(value = 99_999_999, message = "옵션 수량은 1억 미만이어야 합니다.")
    private int quantity;

    public ProductOptionRequestDto() {}


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
