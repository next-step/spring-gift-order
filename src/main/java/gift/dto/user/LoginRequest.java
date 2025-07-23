package gift.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
        @Email
        String email,

        @NotBlank
        String password
) {

    private static final LoginRequest EMPTY = new LoginRequest(null, null);
    public static LoginRequest empty() {
        return EMPTY;
    }
}
