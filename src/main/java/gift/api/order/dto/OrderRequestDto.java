package gift.api.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
        @NotNull(message = "주문할 상품의 옵션 ID는 필수입니다.")
        Long optionId,

        @Min(value = 1, message = "최소 주문 수량은 1개입니다.")
        int quantity,

        String message
) {

}
