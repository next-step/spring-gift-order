package gift.order.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequestDto(
    @NotNull(message = "옵션 ID는 필수입니다.")
    Long optionId,

    @NotNull(message = "주문 수량은 필수입니다.")
    @Min(value = 1, message = "주문 수량은 최소 1개 이상이어야 합니다.")
    @Max(value = 99999999, message = "주문 수량은 1억 개 미만이어야 합니다.")
    Integer quantity,

    @NotEmpty(message = "메시지는 필수입니다.")
    @Size(max = 100, message = "메시지는 100자 이내로 입력해주세요.")
    String message
) {

}
