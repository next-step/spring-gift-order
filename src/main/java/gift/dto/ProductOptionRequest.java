package gift.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ProductOptionRequest(

    @NotBlank(message = "옵션 이름은 필수입니다.")
    @Size(max = 50, message = "옵션 이름은 50자 이하만 입력 가능합니다.")
    @Pattern(regexp = "^[\\s\\w가-힣-+()&/\\[\\]]+$", message = "지원하지 않는 문자가 포함되어있습니다.")
    String name,

    @NotNull(message = "수량은 필수입니다.")
    @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
    @Max(value = 99_999_999, message = "수량은 1억 미만이어야 합니다.")
    Long quantity
) {

    public static ProductOptionRequest from(ProductOptionResponse response) {
        return new ProductOptionRequest(
            response.name(),
            response.quantity()
        );
    }
}
