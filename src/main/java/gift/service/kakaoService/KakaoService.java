package gift.service.kakaoService;

import org.springframework.stereotype.Service;


public interface KakaoService {
    String getAccessTokenFromKakao(String code);

}
