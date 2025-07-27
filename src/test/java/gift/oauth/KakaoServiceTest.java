package gift.oauth;

import gift.authorization.oauth.KakaoClient;
import gift.authorization.oauth.KakaoOAuthProperties;
import gift.authorization.oauth.KakaoService;
import gift.authorization.oauth.dto.KakaoTokenResponseDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;

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

        KakaoTokenResponseDto result = kakaoService.requestAccessToken(code);

        assertEquals("mock-access-token", result.accessToken());
        verify(kakaoClient).requestToken(tokenUri, clientId, redirectUri, code);
    }
}
