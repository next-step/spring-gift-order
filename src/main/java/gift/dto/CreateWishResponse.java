package gift.dto;

import jakarta.validation.constraints.NotNull;

public class CreateWishResponse {

    @NotNull
    private final Long wishId;

    @NotNull
    private final Long productId;

    public CreateWishResponse(Long wishId, Long productId) {
        this.wishId = wishId;
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}