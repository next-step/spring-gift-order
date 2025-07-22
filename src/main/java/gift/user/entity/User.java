package gift.user.entity;

import gift.product.entity.Product;
import gift.shared.domain.UserRole;
import gift.user.dto.request.UserModifyRequest;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role = UserRole.NORMAL;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    protected User() {}

    public Long getId(){
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

    public boolean isPasswordMatched(String password){
        return this.password.equals(password);
    }

    public void modifyUser(UserModifyRequest userModifyRequest){
        String email = userModifyRequest.email();
        if(email != null && !Objects.equals(email, this.email)){
            this.email = email;
        }
        String password = userModifyRequest.password();
        if(password != null && !Objects.equals(password, this.password)){
            this.password = password;
        }
    }
}
