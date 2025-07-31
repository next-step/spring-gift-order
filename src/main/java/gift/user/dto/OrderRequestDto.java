package gift.user.dto;

import jakarta.validation.constraints.Size;

public record OrderRequestDto(
        Long productId,
        Long optionId,
        Long quantity,
        @Size(max = 200)
        String message) {
}
