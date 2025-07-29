package gift.oauth;

import gift.authorization.dto.LoginRequestByKakaoDto;
import gift.authorization.dto.TokenResponseDto;
import gift.authorization.dto.UserRegisterRequestByKakaoDto;
import gift.authorization.oauth.KakaoClient;
import gift.authorization.oauth.KakaoOAuthProperties;
import gift.authorization.oauth.KakaoService;
import gift.authorization.oauth.KakaoTokenService;
import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import gift.authorization.oauth.exception.KakaoLoginRequestException;
import gift.authorization.service.AuthorizationService;
import gift.member.repository.MemberRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @Mock
    private KakaoOAuthProperties kakaoProps;

    @Mock
    private KakaoClient kakaoClient;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private KakaoTokenService kakaoTokenService;

    @InjectMocks
    private KakaoService kakaoService;

    @Test
    void 토큰_발급_및_회원가입_로그인_성공() {
        String code = "mock_code";
        String tokenUri = "https://token.uri";
        String clientId = "client-id";
        String redirectUri = "http://redirect";
        String kakaoAccessToken = "kakao-access-token";
        String kakaoRefreshToken = "kakao-refresh-token";
        String kakaoUserId = "kakao-user-id";

        KakaoTokenResponseDto kakaoToken = new KakaoTokenResponseDto(
                kakaoAccessToken,
                "bearer",
                kakaoRefreshToken,
                3600,
                "profile"
        );

        TokenResponseDto jwtToken = new TokenResponseDto("jwt-token");

        given(kakaoProps.getTokenUri()).willReturn(tokenUri);
        given(kakaoProps.getClientId()).willReturn(clientId);
        given(kakaoProps.getRedirectUri()).willReturn(redirectUri);
        given(kakaoClient.requestToken(tokenUri, clientId, redirectUri, code)).willReturn(kakaoToken);
        given(kakaoClient.requestUserId(kakaoAccessToken)).willReturn(kakaoUserId);
        given(memberRepository.existsByClientId(kakaoUserId)).willReturn(false);
        given(authorizationService.loginMemberByKakao(any(LoginRequestByKakaoDto.class))).willReturn(jwtToken);

        TokenResponseDto result = kakaoService.requestAccessToken(code);

        verify(authorizationService).registerMemberByKakao(new UserRegisterRequestByKakaoDto(kakaoUserId));
        verify(kakaoTokenService).addToken(kakaoUserId, kakaoToken);
        verify(authorizationService).loginMemberByKakao(new LoginRequestByKakaoDto(kakaoUserId));
        assertThat(result.token()).isEqualTo("jwt-token");
    }

    @Test
    void 카카오_토큰발급_실패시_예외_발생() {
        String code = "bad_code";
        String tokenUri = "https://token.uri";
        String clientId = "client-id";
        String redirectUri = "http://redirect";

        given(kakaoProps.getTokenUri()).willReturn(tokenUri);
        given(kakaoProps.getClientId()).willReturn(clientId);
        given(kakaoProps.getRedirectUri()).willReturn(redirectUri);
        given(kakaoClient.requestToken(tokenUri, clientId, redirectUri, code))
                .willThrow(new KakaoLoginRequestException("카카오 토큰 요청 중 오류"));

        assertThatThrownBy(() -> kakaoService.requestAccessToken(code))
                .isInstanceOf(KakaoLoginRequestException.class)
                .hasMessageContaining("카카오 토큰 요청 중 오류");
    }
}
