package gift.dto.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record BasicLoginRequest(
        @Email
        String email,

        @NotBlank
        String password
) implements LoginRequest {

    private static final BasicLoginRequest EMPTY = new BasicLoginRequest(null, null);
    public static BasicLoginRequest empty() {
        return EMPTY;
    }
}
