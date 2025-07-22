package gift.dto;

import gift.entity.Option;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record OptionRequest(
        @NotBlank
        @Size(max = 50, message = "옵션 이름은 공백을 포함하여 최대 50자까지 입력할 수 있습니다.")
        @Pattern(
                regexp = "^[a-zA-Z0-9가-힣\\s()\\[\\]+\\-&/_]*$",
                message = "옵션 이름에 허용되지 않는 특수문자가 포함되어 있습니다."
        )
        String name,

        @Min(value = 1, message = "옵션 수량은 최소 1개 이상이어야 합니다.")
        @Max(value = 99999999, message = "옵션 수량은 1억 개 미만이어야 합니다.")
        int quantity
) {

    public Option toEntity() {
        return new Option(name, quantity);
    }
}