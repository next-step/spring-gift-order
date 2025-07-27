package gift.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "member",
    uniqueConstraints = @UniqueConstraint(columnNames = {"email", "loginType"})
)
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "password", nullable = true)
    String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "loginType", nullable = false)
    LoginType loginType;

    @OneToMany(
        mappedBy = "member",
        cascade = CascadeType.REMOVE,
        orphanRemoval = true
    )
    private List<Wish> wishes = new ArrayList<>();

    protected Member() {
    }

    public Member(String email, String password, LoginType loginType) {
        this(null, email, password, loginType);
    }

    public Member(Long id, String email, String password, LoginType loginType) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.loginType = loginType;
    }

    public Long getId() {
        return this.id;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public List<Wish> getWishes() {
        return wishes;
    }

    public LoginType getLoginType() { return loginType; }

    public boolean matchesPassword(String password) {
        if (!this.password.equals(password)) {
            return false;
        } else {
            return true;
        }
    }

    public void changePassword(String afterPassword) {
        this.password = afterPassword;
    }

    public Wish addWish(Wish wish) {
        wishes.add(wish);
        return wish;
    }

    public void removeWish(Wish wish) {
        wishes.remove(wish);
        wish.setProduct(null);
    }
}
