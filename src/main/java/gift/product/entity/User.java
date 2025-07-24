package gift.product.entity;


import jakarta.persistence.*;


@Entity
@Table(name = "users")
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String email;

	private String password;

	private String nickname;

	public User(Long id, String email, String password, String nickname) {
		this.id = id;
		this.email = email;
		this.password = password;
		this.nickname = nickname;
	}

	public User(String email, String password, String nickname) {
		this(null, email, password, nickname);
	}

	protected User() {}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public String getNickname() {
		return nickname;
	}

}
