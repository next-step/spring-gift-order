package gift.dto.wishListDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWishItemRequestDto(@NotNull String name, @Min(0) Integer quantity) {
}
