package gift.entity;

import gift.domain.member.Email;
import gift.domain.member.Password;
import gift.domain.member.Role;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "members")
public class Member {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private Email email;

    @Embedded
    private Password password;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Wish> wishes = new ArrayList<>();

    protected Member() {}

    public Member(Long id, Email email, Password password, Role role) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Email email, Password password, Role role) {
        this.id = null;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public Member(Email email, Password password) {
        this.id = null;
        this.email = email;
        this.password = password;
        this.role = Role.USER;
    }

    public Long getId() { return id; }
    public Email getEmail() { return email; }
    public Password getPassword() { return password; }
    public Role getRole() { return role; }
}