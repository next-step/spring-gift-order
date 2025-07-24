package gift.entity;

import gift.dto.userDto.UserUpdateDto;
import gift.exception.userException.UserAuthorizationException;
import gift.exception.userException.UserEmailException;
import gift.exception.userException.UserPasswordException;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "member")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String email;

    @Column(nullable = false, length = 30)
    @NotNull
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private UserRole role;

    protected User() {
    }

    public User(String email, String password, UserRole role) {
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public User(Long id, String email, String password, UserRole role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public void checkPassword(String password) {
        if (this.password.equals(password)) {
            throw new UserPasswordException();
        }
    }


    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public UserRole getRole() {
        return role;
    }

    public void changeEmailAndPassword(String newEmail, String newPassword) {

        if (newEmail == null) {
            throw new UserEmailException();
        }

        if (newPassword == null) {
            throw new UserPasswordException();
        }

        this.email = newEmail;
        this.password = newPassword;
    }

    public User updateFrom(String email, String password) {
        return new User(this.id, email, password, this.role);
    }

    public void checkAuthorization() {
        if (this.role == UserRole.USER) {
            throw new UserAuthorizationException();
        }
    }
}