package gift.dto.order;

import jakarta.validation.constraints.Min;

public record OrderUpdateRequest (
    @Min(value = 1, message = "주문 수량은 1 이상이어야 합니다.")
    Integer quantity,
    @Min(value = 0, message = "총 가격은 0 이상이어야 합니다.")
    Long totalPrice
) {

}
