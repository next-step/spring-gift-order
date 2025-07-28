package gift.oauth.service;

import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.member.repository.MemberRepository;
import gift.oauth.domain.Token;
import gift.oauth.dto.KakaoTokenResponseDto;
import gift.oauth.dto.KakaoUserInfoResponseDto;
import gift.oauth.repository.TokenRepository;
import gift.util.JwtUtil;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

@Service
@Transactional(readOnly = true)
public class KakaoService {

    private final MemberRepository memberRepository;
    private final TokenRepository tokenRepository;
    private final RestClient restClient;
    private final JwtUtil jwtUtil;

    @Value("${kakao.client-id}")
    private String clientId;

    @Value("${kakao.client-secret}")
    private String clientSecret;

    @Value("${kakao.redirect-uri}")
    private String redirectUri;

    @Value("${kakao.api.token-uri}")
    private String tokenUri;

    @Value("${kakao.api.user-info-uri}")
    private String userInfoUri;

    public KakaoService(MemberRepository memberRepository, TokenRepository tokenRepository,
            JwtUtil jwtUtil) {
        this.memberRepository = memberRepository;
        this.tokenRepository = tokenRepository;
        this.restClient = RestClient.create();
        this.jwtUtil = jwtUtil;
    }

    @Transactional
    public String login(String code) {
        String accessToken = getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = getUserInfo(accessToken);
        Member member = registerOrLoginUser(userInfo);

        tokenRepository.findByMemberAndProvider(member, "KAKAO")
                .ifPresentOrElse(
                        token -> token.updateAccessToken(accessToken),
                        () -> tokenRepository.save(new Token(member, "KAKAO", accessToken))
                );

        return jwtUtil.createToken(member.getEmail(), member.getRole());
    }

    private String getAccessToken(String code) {
        KakaoTokenResponseDto kakaoTokenResponseDto = restClient.post()
                .uri(tokenUri)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body("grant_type=authorization_code&client_id=" + clientId +
                        "&redirect_uri=" + redirectUri + "&code=" + code +
                        "&client_secret=" + clientSecret)
                .retrieve()
                .body(KakaoTokenResponseDto.class);

        if (kakaoTokenResponseDto == null) {
            throw new RuntimeException("카카오 토큰을 발급받는데 실패했습니다.");
        }

        return kakaoTokenResponseDto.accessToken();
    }

    private KakaoUserInfoResponseDto getUserInfo(String accessToken) {
        KakaoUserInfoResponseDto userInfo = restClient.get()
                .uri(userInfoUri)
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .body(KakaoUserInfoResponseDto.class);

        if (userInfo == null) {
            throw new RuntimeException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }

        return userInfo;
    }

    private Member registerOrLoginUser(KakaoUserInfoResponseDto userInfo) {
        String nickname = userInfo.kakaoAccount().profile().nickname();
        String email = nickname + "@kakao";

        return memberRepository.findByEmail(email)
                .orElseGet(() -> {
                    String password = BCrypt.hashpw("password", BCrypt.gensalt());

                    Member newMember = new Member(
                            email,
                            password,
                            MemberRole.USER
                    );

                    return memberRepository.save(newMember);
                });
    }
}
