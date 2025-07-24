package gift.service;

import gift.auth.jwt.JwtUtil;
import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.dto.AuthUser;
import gift.dto.TokenResponse;
import gift.entity.Member;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KaKaoAuthServiceImpl implements AuthService {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;
    private final RestClient restClient;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.scope}")
    private String scope;
    @Value("${kakao.token-url}")
    private String tokenUrl;
    @Value("${kakao.user-info-url}")
    private String userInfoUrl;

    public KaKaoAuthServiceImpl(MemberService memberService, JwtUtil jwtUtil,
        RestClient.Builder restClient) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.restClient = restClient.build();
    }

    @Override
    public String getRedirectUrl() {
        return UriComponentsBuilder.newInstance()
            .scheme("https")
            .host("kauth.kakao.com")
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", clientId)
            .queryParam("redirect_uri", redirectUri)
            .queryParam("scope", scope)
            .build(true)
            .toUriString();
    }

    @Override
    public AuthUser authenticate(String code) {
        try {
            String accessToken = requestAccessToken(code);
            return requestUserInfo(accessToken);
        } catch (Exception e) {
            throw new CustomException(CustomResponseCode.LOGIN_FAILED);
        }
    }

    @Override
    public TokenResponse registerOrLogin(AuthUser authUser) {
        Member member = memberService.getOrCreate(authUser);
        String token = jwtUtil.generateToken(member);
        return TokenResponse.from(token);
    }

    private String requestAccessToken(String code) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "authorization_code");
        formData.add("client_id", clientId);
        formData.add("redirect_uri", redirectUri);
        formData.add("code", code);
        formData.add("client_secret", clientSecret);

        ResponseEntity<Map> response = restClient.post()
            .uri(tokenUrl)
            .headers(h -> h.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(formData)
            .retrieve()
            .toEntity(Map.class);

        return (String) response.getBody().get("access_token");
    }

    private AuthUser requestUserInfo(String accessToken) {
        ResponseEntity<Map> response = restClient.get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .toEntity(Map.class);

        return AuthUser.fromKakao(response.getBody());
    }
}
