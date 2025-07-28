package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoService {

    private final KakaoOAuthProperties kakaoProps;
    private final KakaoClient kakaoClient;
    private final KakaoOAuthProperties kakaoOAuthProperties;

    public KakaoService(KakaoOAuthProperties kakaoProps, KakaoClient kakaoClient, KakaoOAuthProperties kakaoOAuthProperties) {
        this.kakaoProps = kakaoProps;
        this.kakaoClient = kakaoClient;
        this.kakaoOAuthProperties = kakaoOAuthProperties;
    }

    public String getKakaoLoginUrl() {
        return kakaoOAuthProperties.getKakaoLoginUrl();
    }

    public KakaoTokenResponseDto requestAccessToken(String code) {
        return kakaoClient.requestToken(
                kakaoProps.getTokenUri(),
                kakaoProps.getClientId(),
                kakaoProps.getRedirectUri(),
                code
        );
    }
}
