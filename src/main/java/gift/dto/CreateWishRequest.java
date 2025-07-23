package gift.dto;

import jakarta.validation.constraints.NotNull;

public class CreateWishRequest {

    @NotNull
    private final Long productId;

    public CreateWishRequest(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
