package gift.service.member;

import gift.dto.login.KakaoTokenDto;

public interface OauthService {
    public KakaoTokenDto fetchKakaoToken(String code);

    public String extractEmailFromKakao(String token);
}