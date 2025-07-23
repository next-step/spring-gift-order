package gift.dto.product;

import jakarta.validation.constraints.*;

public record CreateProductOptionRequest(

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        @Size(max = 50, message = "옵션은 공백을 포함하여 50자까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣()\\[\\]+\\-&/_\\s]*$", message = "특수문자는 ( ), [ ], +, -, &, /, _ 만 허용됩니다.")
        String name,

        @NotNull(message = "가격은 필수 입력 값입니다.")
        @Min(value = 0, message = "가격은 0 이상이어야 합니다.")
        @Max(value = 100_000_000, message = "가격은 1억원 이하여야 합니다.")
        Integer price,

        @NotNull(message = "수량은 필수 입력 값입니다.")
        @Min(value = 1, message = "수량은 1 이상이어야 합니다.")
        @Max(value = 99_999_999, message = "수량은 1억원 미만이어야 합니다.")
        Integer quantity
) {

}
