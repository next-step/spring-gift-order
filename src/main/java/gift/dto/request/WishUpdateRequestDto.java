package gift.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record WishUpdateRequestDto(

    @NotBlank
    String productName,

    @Min(1)
    int quantity
) {

}
