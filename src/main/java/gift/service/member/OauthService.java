package gift.service.member;

public interface OauthService {
    public String fetchKakaoToken(String code);

    public String extractEmailFromKakao(String token);
}