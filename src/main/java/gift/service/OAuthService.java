package gift.service;

import gift.auth.JwtTokenProvider;
import gift.client.KakaoClient;
import gift.config.KakaoOauthProperties;
import gift.dto.TokenResponse;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.entity.Member;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final KakaoOauthProperties kakaoOauthProperties;
    private final MemberService memberService;
    private final JwtTokenProvider jwtTokenProvider;
    private final KakaoClient kakaoClient;

    public OAuthService(KakaoOauthProperties kakaoOauthProperties, MemberService memberService,
            JwtTokenProvider jwtTokenProvider, KakaoClient kakaoClient) {
        this.kakaoOauthProperties = kakaoOauthProperties;
        this.memberService = memberService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.kakaoClient = kakaoClient;
    }

    @Transactional
    public TokenResponse loginWithKakao(String code) {
        KakaoTokenResponse kakaoAccessToken = kakaoClient.getKakaoToken(
                "authorization_code",
                kakaoOauthProperties.clientId(),
                kakaoOauthProperties.redirectUri(),
                code,
                kakaoOauthProperties.clientSecret()
        );
        log.info("✅ 카카오 액세스 토큰, ('X-Kakao-Token' 헤더용): {}", kakaoAccessToken.accessToken());

        KakaoUserInfoResponse userInfo = kakaoClient.getKakaoUserInfo(
                kakaoAccessToken.accessToken());

        Member member = memberService.findOrCreateMemberByKakaoId(userInfo.id());

        String jwtToken = jwtTokenProvider.createToken(member.getId().toString());

        log.info("✅ Application JWT ('Authorization' 헤더용): {}", jwtToken);

        return new TokenResponse(jwtToken);
    }
}