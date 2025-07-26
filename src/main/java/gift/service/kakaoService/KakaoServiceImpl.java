package gift.service.kakaoService;

import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class KakaoServiceImpl implements KakaoService {

    private final KakaoProperties kakaoProperties;

    public KakaoServiceImpl(KakaoProperties kakaoProperties) {
        this.kakaoProperties = kakaoProperties;
    }

    @Override
    public String getAccessTokenFromKakao(String authorizationCode) {
        RestClient client = RestClient.create();

        Map<String, String> form = Map.of("grant_type", "authorization_code", "client_id", kakaoProperties.clientId(), "redirect_uri", kakaoProperties.redirectUri(), "code", authorizationCode);

        KakaoTokenResponseDto responseBody = client.post().uri("https://kauth.kakao.com/oauth/token").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(KakaoTokenResponseDto.class);

        if (responseBody == null || responseBody.accessToken() == null) {
            throw new RuntimeException("카카오 토큰 요청 실패");
        }

        return responseBody.accessToken();
    }
}
