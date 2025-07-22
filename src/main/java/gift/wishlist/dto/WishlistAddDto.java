package gift.wishlist.dto;

import jakarta.validation.constraints.NotNull;

public record WishlistAddDto(
    @NotNull(message = "itemId는 필수입니다.")
    Long itemId
) {

}
