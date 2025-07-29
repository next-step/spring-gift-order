package gift.oauth;

import gift.authorization.dto.TokenResponseDto;
import gift.authorization.oauth.KakaoClient;
import gift.authorization.oauth.KakaoOAuthProperties;
import gift.authorization.oauth.KakaoService;
import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import gift.authorization.oauth.exception.KakaoLoginRequestException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class KakaoServiceTest {

    @Mock
    private KakaoOAuthProperties kakaoProps;

    @Mock
    private KakaoClient kakaoClient;

    @InjectMocks
    private KakaoService kakaoService;

    @Test
    void 토큰_발급_요청_후_토큰_반환() {
        String code = "mock_code";
        String tokenUri = "https://mock.token.uri";
        String clientId = "mock_client_id";
        String redirectUri = "http://mock.redirect";

        KakaoTokenResponseDto mockResponse = new KakaoTokenResponseDto(
                "mock-access-token",
                "bearer",
                "mock-refresh-token",
                3600,
                "profile"
        );

        given(kakaoProps.getTokenUri()).willReturn(tokenUri);
        given(kakaoProps.getClientId()).willReturn(clientId);
        given(kakaoProps.getRedirectUri()).willReturn(redirectUri);
        given(kakaoClient.requestToken(tokenUri, clientId, redirectUri, code)).willReturn(mockResponse);
        given(kakaoClient.requestUserId(mockResponse.accessToken())).willReturn("mock-user-id");
        TokenResponseDto result = kakaoService.requestAccessToken(clientId);

        assertThat(result.token()).isEqualTo("mock-access-token");
    }

    @Test
    void 토큰_발급_실패시_커스텀예외_발생() {
        String code = "bad_code";
        String tokenUri = "https://mock.token.uri";
        String clientId = "mock_client_id";
        String redirectUri = "http://mock.redirect";

        given(kakaoProps.getTokenUri()).willReturn(tokenUri);
        given(kakaoProps.getClientId()).willReturn(clientId);
        given(kakaoProps.getRedirectUri()).willReturn(redirectUri);
        given(kakaoClient.requestToken(tokenUri, clientId, redirectUri, code))
                .willThrow(new KakaoLoginRequestException("카카오 토큰 요청 중 오류 발생"));


        assertThatThrownBy(() -> kakaoService.requestAccessToken(code))
                .isInstanceOf(KakaoLoginRequestException.class)
                .hasMessageContaining("카카오 토큰 요청 중 오류 발생");
    }
}
