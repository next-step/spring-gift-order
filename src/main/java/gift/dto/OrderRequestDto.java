package gift.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequestDto (
        @NotNull(message = "옵션 id는 필수입니다.")
        Long optionId,
        @NotNull(message = "상품 수량은 필수입니다.")
        Integer quantity,
        String message
){
}
