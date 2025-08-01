package gift.entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "kakao_token_id")
    private KakaoToken kakaoToken;

    @OneToMany(mappedBy = "user")
    private List<Wish> wishList = new ArrayList<>();

    protected User() {}

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public void update(String email, String password){
        this.email = email;
        this.password = password;
    }

    public void updateKakaoToken(KakaoToken kakaoToken){
        this.kakaoToken = kakaoToken;
    }

    public Long getId() {return id;}
    public String getEmail() {return email;}
    public String getPassword() {return password;}
    public LocalDateTime getCreatedDate() {return createdDate;}
    public String getRole() {return role;}
    public KakaoToken getKakaoToken() {return kakaoToken;}
    public List<Wish> getWishList() {return wishList;}
}
