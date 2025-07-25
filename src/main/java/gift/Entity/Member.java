package gift.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "members")
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "닉네임은 필수입니다.")
    @Size(min = 5, message = "닉네임은 5자 이상이어야 합니다.")
    @Column(unique = true)
    private String nickname;

    @Column(nullable = true)
    @Email(message = "유효한 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 9, message = "비밀번호는 9자 이상이어야 합니다.")
    private String password;

    @Column(nullable = true)
    private String name;

    @Column(nullable = true)
    private String address;

    private String role = "USER";  // 기본 권한

    // 생성자
    public Member() {}

    public Member(String nickname, String email, String password, String name, String address, String role) {
        this.nickname = nickname;
        this.email = email;
        this.password = password;
        this.name = name;
        this.address = address;
        this.role = role;
    }

    // Getter
    public String getNickname() { return nickname; }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getRole() { return role; }

    // Setter
    public void  setId(Long id) { this.id = id; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setAddress(String address) { this.address = address; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() {
        return "ADMIN".equalsIgnoreCase(this.role);
    }

}
