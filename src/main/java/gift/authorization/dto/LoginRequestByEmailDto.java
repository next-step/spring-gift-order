package gift.authorization.dto;

import jakarta.validation.constraints.Email;

public record LoginRequestByEmailDto(
        @Email
        String email,
        String password) {
}
