package gift.dto.orderDto;

import java.time.LocalDateTime;

public record OrderResponseDto(Long id, Long optionId, Integer quantity, LocalDateTime orderDateTime, String message) {
}
