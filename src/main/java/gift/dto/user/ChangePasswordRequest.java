package gift.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ChangePasswordRequest(
        @Email
        String email,

        @NotBlank
        String oldPassword,

        @NotBlank
        String newPassword) {
}
