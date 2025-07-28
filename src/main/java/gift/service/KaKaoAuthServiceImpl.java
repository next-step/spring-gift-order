package gift.service;

import gift.auth.jwt.JwtUtil;
import gift.common.code.CustomResponseCode;
import gift.common.exception.ForbiddenException;
import gift.common.exception.ServerErrorException;
import gift.common.exception.UnauthorizedException;
import gift.common.exception.ValidationException;
import gift.dto.AuthUser;
import gift.dto.KaKaoTokenInfo;
import gift.dto.KaKaoUserInfo;
import gift.dto.TokenResponse;
import gift.entity.Member;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
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
        String accessToken = requestAccessToken(code);
        return requestUserInfo(accessToken);
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

        KaKaoTokenInfo tokenInfo = restClient.post()
            .uri(tokenUrl)
            .headers(h -> h.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(formData)
            .retrieve()
            .onStatus(status -> status.value() == 400,
                (req, res) -> {
                    throw new ValidationException();
                })
            .onStatus(status -> status.value() == 401,
                (req, res) -> {
                    throw new UnauthorizedException();
                })
            .onStatus(status -> status.value() == 403,
                (req, res) -> {
                    throw new ForbiddenException();
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (req, res) -> {
                    throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
                })
            .body(KaKaoTokenInfo.class);

        return tokenInfo.accessToken();
    }

    private AuthUser requestUserInfo(String accessToken) {
        ResponseEntity<KaKaoUserInfo> userInfo = restClient.get()
            .uri(userInfoUrl)
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .onStatus(status -> status.value() == 400,
                (req, res) -> {
                    throw new ValidationException();
                })
            .onStatus(status -> status.value() == 401,
                (req, res) -> {
                    throw new UnauthorizedException();
                })
            .onStatus(status -> status.value() == 403,
                (req, res) -> {
                    throw new ForbiddenException();
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (req, res) -> {
                    throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
                })
            .toEntity(KaKaoUserInfo.class);

        return AuthUser.fromKakao(userInfo.getBody());
    }
}
