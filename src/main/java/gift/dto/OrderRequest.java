package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record OrderRequest(
        @JsonProperty("product_id")
        Long productId,

        @JsonProperty("option_id")
        Long optionId,

        int quantity,

        String message
) {

}
