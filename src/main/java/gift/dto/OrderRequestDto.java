package gift.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record OrderRequestDto(
    @NotNull(message = "옵션 ID는 필수입니다.")
    Long optionId,
    @NotNull(message = "해당 옵션에 대해서 구매할 개수는 필수입니다.")
    @Min(value = 1, message = "해당 옵션에 대해서 구매할 개수는 최소 1개 이상이어야 합니다.")
    Integer quantity,
    String message
) {}
