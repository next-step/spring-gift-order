package gift.service;

import gift.common.exception.KakaoLoginException;
import gift.dto.kakao.KakaoTokenResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(value = KakaoTokenProvider.class)
public class KakaoTokenProviderTest {

    @Autowired
    private KakaoTokenProvider kakaoTokenProvider;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    @DisplayName("카카오 로그인 - 정상 응답 시 엑세스 토큰을 반환한다.")
    void test1() {
        String code = "sample_code";

        String responseBody = """
        {
          "access_token": "sample_access_token",
          "refresh_token": "sample_refresh_token",
          "expires_in": 3600,
          "token_type": "bearer",
          "scope": "account_email"
        }
        """;

        this.mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        KakaoTokenResponse token = kakaoTokenProvider.getAccessToken(code);

        Assertions.assertThat("sample_access_token").isEqualTo(token.accessToken());
        mockServer.verify();
    }

    @Test
    @DisplayName("카카오 로그인 - 응답 중 에러가 생길 경우 KakaoLoginException 예외를 반환한다.")
    void test2() {
        String code = "sample_code";

        this.mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
                .andRespond(withBadRequest());

        assertThatThrownBy(() -> kakaoTokenProvider.getAccessToken(code)).isInstanceOf(KakaoLoginException.class);
    }
}
