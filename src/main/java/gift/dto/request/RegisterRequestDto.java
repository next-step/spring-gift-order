package gift.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterRequestDto(

    @NotBlank
    String memberRole,

    @Email
    String email,

    @NotBlank
    String password
) {

}
