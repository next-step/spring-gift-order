package gift.product.dto.request;

import gift.product.entity.Option;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionCreateRequest(
        @Size(min = 1, max = 50)
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣 \\(\\)\\[\\]\\+\\-\\&/_\\.]*$",
                message = "특수 문자는 ( ), [ ], +, -, &, /, _ 만 가능합니다."
        )
        String name,
        @Min(1)
        @Max(99999999)
        Integer quantity
) {
}
