package gift.authorization.oauth;

import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KakaoService {

    private final KakaoOAuthProperties kakaoProps;
    private final KakaoClient kakaoClient;

    public KakaoService(KakaoOAuthProperties kakaoProps, KakaoClient kakaoClient) {
        this.kakaoProps = kakaoProps;
        this.kakaoClient = kakaoClient;
    }

    public String getKakaoLoginUrl() {
        return UriComponentsBuilder
                .fromUriString("https://kauth.kakao.com/oauth/authorize")
                .queryParam("response_type", "code")
                .queryParam("client_id", kakaoProps.getClientId())
                .queryParam("redirect_uri", kakaoProps.getRedirectUri())
                .build()
                .toUriString();
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
