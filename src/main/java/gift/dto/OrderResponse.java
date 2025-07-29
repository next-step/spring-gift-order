package gift.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

public record OrderResponse(

        Long id,

        @JsonProperty("product_id")
        Long productId,

        @JsonProperty("option_id")
        Long opitonId,

        int quantity,

        String message,

        LocalDateTime orderdateTime
) {

}
