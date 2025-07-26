package gift.domain;

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
    
    @Column(nullable = false, unique = true)
    private String email;
    
    @Column(nullable = false)
    private String password;

    protected Member() {
        // JPA 기본 생성자
    }

    private Member(Long id, String email, String password) {
        validateId(id);
        this.id = id;
        this.email = email;
        this.password = password;
    }

    public static Member of(String email, String password) {
        return new Member(null, email, password);
    }

    public static Member withId(Long id, String email, String password) {
        return new Member(id, email, password);
    }

    public Long id() {
        return id;
    }

    public String email() {
        return email;
    }

    public String password() {
        return password;
    }

    public void assignId(Long id) {
        this.id = id;
    }

    public void changeEmail(String email) {
        this.email = email;
    }

    public void changePassword(String password) {
        this.password = password;
    }

    private static void validateId(Long id) {
        if (id != null && id < 0) {
            throw new IllegalArgumentException("ID는 음수일 수 없습니다.");
        }
    }
}
