package gift.entity;

import gift.entity.vo.Email;
import gift.entity.vo.Password;
import gift.enums.UserType;
import jakarta.persistence.*;

@Entity
@Table(name = "users", uniqueConstraints = {@UniqueConstraint(name = "uk_user", columnNames = "email")})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "email", nullable = false))
    private Email email;

    @Embedded
    @AttributeOverride(name = "value", column = @Column(name = "password", nullable = false))
    private Password password;

    @Enumerated(EnumType.STRING)
    private UserType type;

    protected User() {
    }

    public User(Email email, Password password, UserType type) {
        this.email = email;
        this.password = password;
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public Email email() {
        return email;
    }

    public Password password() {
        return password;
    }

    public UserType getType() {
        return type;
    }
}
