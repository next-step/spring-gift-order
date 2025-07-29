package gift.service.Auth;

import gift.auth.jwt.JwtUtil;
import gift.dto.auth.AuthUser;
import gift.dto.auth.KaKaoTokenInfo;
import gift.dto.auth.KaKaoUserInfo;
import gift.dto.auth.TokenResponse;
import gift.entity.Member;
import gift.external.KaKaoTokenClient;
import gift.service.Member.MemberService;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class KaKaoAuthServiceImpl implements AuthService {

    private final MemberService memberService;
    private final JwtUtil jwtUtil;

    private final KaKaoTokenClient kaKaoTokenClient;

    @Value("${kakao.client-id}")
    private String clientId;
    @Value("${kakao.client-secret}")
    private String clientSecret;
    @Value("${kakao.redirect-uri}")
    private String redirectUri;
    @Value("${kakao.scope}")
    private String scope;

    public KaKaoAuthServiceImpl(MemberService memberService, JwtUtil jwtUtil,
        KaKaoTokenClient kaKaoTokenClient) {
        this.memberService = memberService;
        this.jwtUtil = jwtUtil;
        this.kaKaoTokenClient = kaKaoTokenClient;
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
        KaKaoUserInfo userInfo = kaKaoTokenClient.requestUserInfo(tokenInfo.accessToken());

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
        return kaKaoTokenClient.requestToken(params);
    }

    private KaKaoTokenInfo requestTokenByRefreshToken(String refreshToken) {
        Map<String, String> params = Map.of(
            "grant_type", "refresh_token",
            "client_id", clientId,
            "refresh_token", refreshToken
        );
        return kaKaoTokenClient.requestToken(params);
    }
}