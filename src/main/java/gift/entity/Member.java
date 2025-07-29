package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "login_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private LoginType loginType = LoginType.REGULAR;

    @Column(name = "type_id")
    private String typeId;

    protected Member() {
    }

    public Member(String email, String password) {
        this.email = email;
        this.password = password;
        this.loginType = LoginType.REGULAR;
    }

    public Member(String email, LoginType loginType, String typeId) {
        this.email = email;
        this.password = UUID.randomUUID().toString();
        this.loginType = loginType;
        this.typeId = typeId;
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

    public LoginType getLoginType() {
        return loginType;
    }

    public String getTypeId() {
        return typeId;
    }
}