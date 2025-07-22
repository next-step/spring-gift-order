package gift.user.dto.request;

import gift.user.entity.User;

public record UserModifyRequest(
        String email,
        String password
) {
    public User toEntity() {
        return new User(
            this.email(),
            this.password()
        );
    }
}
