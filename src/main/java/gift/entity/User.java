package gift.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name= "user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String password;

    @CreatedDate
    private LocalDateTime createdDate;

    private String role;

    protected User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void update(String email, String password){
        this.email = email;
        this.password = password;
    }

    public Long getId() {return id;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public LocalDateTime getCreatedDate() {return createdDate;}
    public String getRole() {return role;}
}
