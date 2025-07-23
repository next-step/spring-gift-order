package gift.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;

public record ChangeRoleRequest(
        @Email
        String email,

        @Pattern(regexp = "^(ADMIN|USER)$", message = "역할은 ADMIN 또는 USER여야 합니다.")
        String role) {
}
