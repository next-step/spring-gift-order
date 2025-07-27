package gift.dto.kakao;

public class LoginResponse {

    private final Long id;
    private final String nickname;
    private final String token;

    public LoginResponse(Long id, String nickname, String token) {
        this.id = id;
        this.nickname = nickname;
        this.token = token;
    }

    public Long getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getToken() {
        return token;
    }
}