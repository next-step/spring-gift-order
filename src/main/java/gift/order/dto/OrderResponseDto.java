package gift.order.dto;

import java.time.LocalDateTime;

public record OrderResponseDto(
    Long id,
    Long optionId,
    Integer quantity,
    String message,
    LocalDateTime createdAt
) {

}
