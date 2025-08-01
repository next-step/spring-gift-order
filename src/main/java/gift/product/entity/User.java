package gift.product.entity;


import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String nickname;

	private Long oauthId;

	@OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
	private KakaoToken kakaoToken;

	private User(Long id, String nickname, Long oauthId) {
		this.id = id;
		this.nickname = nickname;
		this.oauthId = oauthId;
	}

	public User(String nickname, Long oauthId) {
		// 팩토리 메서드로서 속성에 대한 검증 들어갈 곳

		this(null, nickname, oauthId);
	}

	protected User() {}

	public Long getId() {
		return id;
	}

	public String getNickname() {
		return nickname;
	}

	public Long getOauthId() {
		return oauthId;
	}

}
