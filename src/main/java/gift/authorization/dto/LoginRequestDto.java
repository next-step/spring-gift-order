package gift.authorization.dto;

import jakarta.validation.constraints.Email;

public record LoginRequestDto(
        @Email
        String email,
        String password) {
}
