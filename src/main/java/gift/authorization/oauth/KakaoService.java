package gift.authorization.oauth;

import gift.authorization.dto.LoginRequestByKakaoDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestByKakaoDto;
import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import gift.authorization.oauth.exception.KakaoLoginRequestException;
import gift.authorization.service.AuthorizationService;
import gift.member.repository.MemberRepository;
import org.springframework.stereotype.Service;

@Service
public class KakaoService {

    private final KakaoOAuthProperties kakaoProps;
    private final KakaoClient kakaoClient;
    private final MemberRepository memberRepository;
    private final AuthorizationService authorizationService;
    private final KakaoTokenService kakaoTokenService;

    public KakaoService(KakaoOAuthProperties kakaoProps, KakaoClient kakaoClient, MemberRepository memberRepository, AuthorizationService authorizationService, KakaoTokenService kakaoTokenService) {
        this.kakaoProps = kakaoProps;
        this.kakaoClient = kakaoClient;
        this.memberRepository = memberRepository;
        this.authorizationService = authorizationService;
        this.kakaoTokenService = kakaoTokenService;
    }

    public String getKakaoLoginUrl() {
        return kakaoProps.getKakaoLoginUrl();
    }

    public TokenResponseDto requestAccessToken(String code) {
        //카카오 토큰 요청
        KakaoTokenResponseDto token = kakaoClient.requestToken(
                kakaoProps.getTokenUri(),
                kakaoProps.getClientId(),
                kakaoProps.getRedirectUri(),
                code
        );
        //client id 추출
        String clientId = kakaoClient.requestUserId(token.accessToken());
        boolean existed = memberRepository.existsByClientId(clientId);

        if (!existed){ //회원가입이 안되있다면
            authorizationService.registerMemberByKakao(new UserRegisterRequestByKakaoDto(clientId));
        }

        //액세스 토큰을 레포지터리에 저장
        kakaoTokenService.addToken(clientId, token);

        //로그인 후 토큰발급
        return authorizationService.loginMemberByKakao(new LoginRequestByKakaoDto(clientId));




    }
}
