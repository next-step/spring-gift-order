package gift.dto.orderDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record OrderRequestDto(@NotNull(message = "상품 옵션은 필수 선택입니다.") Long optionId,
                              @Size(min = 1, max = 100_000_000) Integer quantity,
                              String message) {
}
