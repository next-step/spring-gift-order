package gift.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WishDeleteRequestDto(
    @NotBlank
    String productName
) {

}
