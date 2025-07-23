package gift.domain;

import gift.dto.member.MemberRequest;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name="members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column
    private String password;

    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    private List<WishList> wishLists;

    public Member(Long id, String email, String password){
        this.id=id;
        this.email=email;
        this.password=password;
    }

    protected Member() {}

    public Long getId(){
        return this.id;
    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public static Member of(String email, String rawPassword, PasswordEncoder passwordEncoder){
        return new Member(null, email, passwordEncoder.encode(rawPassword));
    }

    public void update(MemberRequest request, PasswordEncoder passwordEncoder){
        this.email= request.email();
        this.password=passwordEncoder.encode(request.password());
    }

    public boolean matches(String rawPassword, PasswordEncoder passwordEncoder){
        return passwordEncoder.matches(rawPassword, this.password);
    }
}
