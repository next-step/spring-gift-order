package gift.domain.user;

import gift.common.exception.InvalidUserException;
import gift.domain.Role;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("BASIC")
public class BasicUser extends User {

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    protected BasicUser(String email, String password, Role role) {
        super(role);
        this.email = email;
        this.password = password;
    }

    @Override
    public void comparePassword(String password) {
        if (!password.equals(this.password)) {
            throw new InvalidUserException();
        }
    }

    @Override
    public void changePassword(String password) {
        this.password = password;
    }

    protected BasicUser() {

    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
