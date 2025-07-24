package gift.service;

import gift.auth.JwtTokenProvider;
import gift.config.KakaoOauthProperties;
import gift.dto.TokenResponse;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.entity.Member;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class OAuthService {

    private final KakaoOauthProperties kakaoOauthProperties;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;

    public OAuthService(KakaoOauthProperties kakaoOauthProperties, MemberService memberService,
            JwtTokenProvider jwtTokenProvider) {
        this.kakaoOauthProperties = kakaoOauthProperties;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Transactional
    public TokenResponse loginWithKakao(String code) {
        KakaoTokenResponse kakaoToken = getKakaoToken(code);
        KakaoUserInfoResponse userInfo = getKakaoUserInfo(kakaoToken.accessToken());

        Member member = memberService.findOrCreateMember(userInfo.getEmail());

        String accessToken = jwtTokenProvider.createToken(member.getId().toString());
        return new TokenResponse(accessToken);
    }

    private KakaoTokenResponse getKakaoToken(String code) {
        String url = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", kakaoOauthProperties.getClientId());
        body.add("redirect_uri", kakaoOauthProperties.getRedirectUri());
        body.add("code", code);
        body.add("client_secret", kakaoOauthProperties.getClientSecret());

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();

        ResponseEntity<KakaoTokenResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                KakaoTokenResponse.class
        );

        return responseEntity.getBody();
    }

    private KakaoUserInfoResponse getKakaoUserInfo(String accessToken) {
        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KakaoUserInfoResponse> responseEntity = restTemplate.exchange(
                url,
                HttpMethod.POST,
                requestEntity,
                KakaoUserInfoResponse.class
        );

        return responseEntity.getBody();
    }
}
