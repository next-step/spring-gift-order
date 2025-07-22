package gift.dto.option;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionRequestDto(
        @NotBlank(message = "옵션 이름은 필수입니다.")
        @Size(max = 50, message = "옵션 이름은 공백을 포함하여 최대 50자까지 입력할 수 있습니다.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎ\\s()\\[\\]+\\-&/_]*$", message = "사용할 수 없는 특수문자가 포함되어 있습니다. ( ), [ ], +, -, &, /, _ 만 사용 가능합니다.")
        String name,

        @NotNull(message = "수량은 필수입니다.")
        @Min(value = 1, message = "수량은 1개 이상이어야 합니다.")
        @Max(value = 99_999_999, message = "수량은 1억 개 미만이어야 합니다.")
        Integer quantity
) {
}