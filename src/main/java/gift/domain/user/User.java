package gift.domain.user;

import gift.domain.Role;
import jakarta.persistence.*;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "loginType", discriminatorType = DiscriminatorType.STRING)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private Role role;

    public LoginType getLoginType() {
        return LoginType.valueOf(this.getClass().getAnnotation(DiscriminatorValue.class).value());
    }

    public abstract void changePassword(String password);

    public abstract void comparePassword(String password);

    public static User createKakaoUser(Long kakaoId, String accessToken, Role role) {
        return new KakaoUser(kakaoId, accessToken, role);
    }

    public static User createBasicUser(String email, String password, Role role) {
        return new BasicUser(email, password, role);
    }

    public User(Role role) {
        this.role = role;
    }

    public void changeRole(String role) {
        this.role = Role.valueOf(role);
    }

    public Long getId() {
        return id;
    }

    public Role getRole() {
        return role;
    }

    protected User() {
    }
}
