package gift.dto.wishlist;

import jakarta.validation.constraints.NotNull;

public record CreateWishlistRequest(
        @NotNull(message = "상품 아이디는 필수 항목입니다.")
        Long productId
) {
}
