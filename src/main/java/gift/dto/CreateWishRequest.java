package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;

public class CreateWishRequest {

    @NotNull
    @JsonProperty("product_id")
    private final Long productId;

    public CreateWishRequest(Long productId) {
        this.productId = productId;
    }

    public Long getProductId() {
        return productId;
    }
}
