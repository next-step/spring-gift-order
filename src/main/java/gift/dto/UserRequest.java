package gift.dto;

import gift.entity.User;
import gift.entity.vo.Email;
import gift.entity.vo.Password;

public class UserRequest {

    private final Email email;
    private final Password password;

    public UserRequest(Email email, Password password) {
        this.email = email;
        this.password = password;
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }

    public User toEntity() {
        return new User(email, password);
    }
}
