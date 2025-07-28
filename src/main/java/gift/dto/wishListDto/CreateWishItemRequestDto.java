package gift.dto.wishListDto;

import gift.entity.ItemOption;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CreateWishItemRequestDto(@NotNull String name, @Min(0) Integer quantity) {
    public ItemOption toEntity() {
        return new ItemOption(null, this.name, this.quantity);
    }
}
