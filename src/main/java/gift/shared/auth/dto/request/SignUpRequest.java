package gift.shared.auth.dto.request;

import gift.user.entity.User;
import jakarta.validation.constraints.Email;

public record SignUpRequest(
        @Email
        String email,
        String password
) {
        public User toEntity() {
                return new User(this.email(), this.password());
        }
}
