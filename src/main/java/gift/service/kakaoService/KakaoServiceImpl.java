package gift.service.kakaoService;

import gift.Jwt.JwtUtil;
import gift.config.KakaoProperties;
import gift.dto.KakaoTokenResponseDto;
import gift.dto.KakaoUserInfoDto;
import gift.entity.User;
import gift.service.userService.UserService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


@Service
public class KakaoServiceImpl implements KakaoService {

    private final KakaoProperties kakaoProperties;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public KakaoServiceImpl(KakaoProperties kakaoProperties, JwtUtil jwtUtil, UserService userService) {
        this.kakaoProperties = kakaoProperties;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @Override
    public String getAccessTokenFromKakao(String authorizationCode) {
        RestClient client = RestClient.create();

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("client_id", kakaoProperties.clientId());
        form.add("redirect_uri", kakaoProperties.redirectUri());
        form.add("code", authorizationCode);
        KakaoTokenResponseDto responseBody = client.post().uri("https://kauth.kakao.com/oauth/token").contentType(MediaType.APPLICATION_FORM_URLENCODED).body(form).retrieve().body(KakaoTokenResponseDto.class);

        if (responseBody == null || responseBody.accessToken() == null) {
            throw new RuntimeException("카카오 토큰 요청 실패");
        }

        String kakaoAccessToken = responseBody.accessToken();
        KakaoUserInfoDto kakaoUserInfo = getUserInfo(kakaoAccessToken);
        User user = userService.saveSocialUser(kakaoUserInfo.getEmail());

        return jwtUtil.generateToken(user);
    }

    private KakaoUserInfoDto getUserInfo(String kakaoAccessToken) {
        return RestClient.create().get()
                .uri("https://kapi.kakao.com/v2/user/me")
                .header("Authorization", "Bearer " + kakaoAccessToken)
                .retrieve()
                .body(KakaoUserInfoDto.class);
    }
}
