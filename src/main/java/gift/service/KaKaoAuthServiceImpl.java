package gift.service;

import gift.auth.jwt.JwtUtil;
import gift.common.code.CustomResponseCode;
import gift.common.exception.ForbiddenException;
import gift.common.exception.KaKaoClientException;
import gift.common.exception.ServerErrorException;
import gift.common.exception.UnauthorizedException;
import gift.common.exception.ValidationException;
import gift.dto.AuthUser;
import gift.dto.KaKaoTokenInfo;
import gift.dto.KaKaoUserInfo;
import gift.dto.TokenResponse;
import gift.entity.Member;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
        KaKaoTokenInfo tokenInfo = requestTokenByCode(code);
        KaKaoUserInfo userInfo = requestUserInfo(tokenInfo.accessToken());

        return AuthUser.fromKakao(userInfo, tokenInfo);
    }

    @Override
    @Transactional
    public TokenResponse registerOrLogin(AuthUser authUser) {
        Member member = memberService.getOrCreate(authUser);
        member.updateTokens(authUser.accessToken(), authUser.refreshToken());
        String token = jwtUtil.generateToken(member);
        return TokenResponse.from(token);
    }

    @Override
    @Transactional
    public String refreshAccessToken(String refreshToken) {
        KaKaoTokenInfo tokenInfo = requestTokenByRefreshToken(refreshToken);

        Member member = memberService.findByRefreshToken(refreshToken);

        member.updateTokens(tokenInfo.accessToken(), tokenInfo.refreshToken());

        return tokenInfo.accessToken();
    }

    private KaKaoTokenInfo requestTokenByCode(String code) {
        Map<String, String> params = Map.of(
            "grant_type", "authorization_code",
            "client_id", clientId,
            "redirect_uri", redirectUri,
            "code", code,
            "client_secret", clientSecret
        );
        return requestToken(params);
    }

    private KaKaoTokenInfo requestTokenByRefreshToken(String refreshToken) {
        Map<String, String> params = Map.of(
            "grant_type", "refresh_token",
            "client_id", clientId,
            "refresh_token", refreshToken
        );
        return requestToken(params);
    }

    private KaKaoTokenInfo requestToken(Map<String, String> params) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        params.forEach(formData::add);

        return restClient.post()
            .uri(tokenUrl)
            .headers(h -> h.setContentType(MediaType.APPLICATION_FORM_URLENCODED))
            .body(formData)
            .retrieve()
            .onStatus(status -> status.value() == 400, (req, res) -> {
                throw new ValidationException();
            })
            .onStatus(status -> status.value() == 401, (req, res) -> {
                throw new UnauthorizedException();
            })
            .onStatus(status -> status.value() == 403, (req, res) -> {
                throw new ForbiddenException();
            })
            .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                throw new KaKaoClientException();
            })
            .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
            })
            .body(KaKaoTokenInfo.class);
    }

    private KaKaoUserInfo requestUserInfo(String accessToken) {
        KaKaoUserInfo userInfo = restClient.get()
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
            .onStatus(HttpStatusCode::is4xxClientError,
                (req, res) -> {
                    throw new KaKaoClientException();
                })
            .onStatus(HttpStatusCode::is5xxServerError,
                (req, res) -> {
                    throw new ServerErrorException(CustomResponseCode.SERVER_ERROR);
                })
            .body(KaKaoUserInfo.class);

        return userInfo;
    }
}
