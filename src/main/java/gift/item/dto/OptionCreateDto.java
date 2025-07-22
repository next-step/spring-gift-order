package gift.item.dto;

import gift.common.validation.OnlyPermittedSymbols;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OptionCreateDto(
    @NotBlank(message = "옵션명은 필수입니다.")
    @Size(max = 50, message = "옵션명은 공백포함 최대 50자까지 입력할 수 있습니다.")
    @OnlyPermittedSymbols
    String name,

    @NotNull(message = "옵션 수량은 필수입니다.")
    @Min(value = 1, message = "옵션 수량은 최소 1개 이상이어야 합니다.")
    @Max(value = 99999999, message = "옵션 수량은 1억 개 미만이어야 합니다.")
    Integer quantity
) {

}
