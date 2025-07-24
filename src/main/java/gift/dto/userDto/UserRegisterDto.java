package gift.dto.userDto;

import gift.entity.User;
import gift.entity.UserRole;
import gift.validation.userPolicy.UserFieldValid;

@UserFieldValid
public record UserRegisterDto(String email, String password, UserRole role) {

    public User dtoToUser() {
        return new User(null, email, password, role);
    }
}
