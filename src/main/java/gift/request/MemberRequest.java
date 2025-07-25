package gift.request;

public class MemberRequest {
    private final String nickname;
    private final String password;

    public MemberRequest(String nickname, String password) {
        this.nickname = nickname;
        this.password = password;
    }

    public String getNickname() { return nickname; }
    public String getPassword() { return password; }
}
