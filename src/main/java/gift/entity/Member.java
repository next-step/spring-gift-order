package gift.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "member")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    private String email;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    public Member() {
    }

    public static Member of(String email, String password) {
        return new Member(null, email, password);
    }

    public static Member withId(Long id, String email, String password) {
        return new Member(id, email, password);
    }

    private Member(Long id, String email, String password) {
        this.id = id;
        this.email = email;
        this.password = password;
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


    public void validatePassword(String password) throws IllegalAccessException {
        if (!this.password.equals(password)) {
            throw new IllegalAccessException("비밀번호가 일치하지 않습니다.");
        }
    }
}
