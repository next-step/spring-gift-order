package gift.dto.request;

import jakarta.validation.constraints.NotBlank;

public record WishAddRequestDto(

    @NotBlank
    String productName

) {

}
