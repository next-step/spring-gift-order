package gift.domain;

import jakarta.persistence.*;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = true, length = 320)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Role role;

    public Member(){
    }

    public Member(Long id, String email, String password, gift.domain.Role role){
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Long id) {
        this.id = id;
    }

    public Member(String email, String password){
        this(null, email, password, Role.USER);
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

    public Role getRole() {
        return role;
    }
}
