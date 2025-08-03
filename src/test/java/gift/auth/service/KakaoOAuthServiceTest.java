package gift.auth.service;

import gift.auth.config.ApiClientConfig;
import gift.auth.dto.KakaoTokenResponseDto;
import gift.auth.exception.KakaoAuthException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;
import org.springframework.test.web.client.response.DefaultResponseCreator;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(KakaoOAuthService.class)
@Import(ApiClientConfig.class)
@TestPropertySource(properties = {
        "kakao.client-id=test-client",
        "kakao.redirect-uri=http://localhost:8080"
})
class KakaoOAuthServiceTest {

    private static final String TOKEN_URL    = "https://kauth.kakao.com/oauth/token";
    private static final String CLIENT_ID    = "test-client";
    private static final String REDIRECT_URI = "http://localhost:8080";

    @Autowired
    private KakaoOAuthService service;

    @Autowired
    private RestTemplate restTemplate;

    private MockRestServiceServer server;

    @BeforeEach
    void setUp() {
        server = MockRestServiceServer.createServer(restTemplate);
    }

    @Test
    @DisplayName("성공: 올바른 인가 코드로 access token을 반환한다. ")
    void requestAccessToken_success() {
        String body = """
            {
              "token_type":"bearer",
              "access_token":"access123",
              "refresh_token":"refresh123",
              "expires_in":12345678
            }
            """;

        commonExpect("auth-code")
                .andRespond(withSuccess(body, MediaType.APPLICATION_JSON));

        KakaoTokenResponseDto response = service.requestAccessToken("auth-code");

        assertThat(response.accessToken()).isEqualTo("access123");
        assertThat(response.refreshToken()).isEqualTo("refresh123");
        assertThat(response.expiresIn().getEpochSecond()).isEqualTo(12345678);

        server.verify();
    }

    @Test
    @DisplayName("invalid_request 에러: 잘못된 파라미터 예외가 발생한다. ")
    void error_invalidRequest() {
        performErrorTest(
                "invalid_request",
                "잘못된 파라미터입니다. ",
                "잘못된 파라미터입니다. ",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("invalid_client 에러: 앱 키 오류 예외가 발생한다. ")
    void error_invalidClient() {
        performErrorTest(
                "invalid_client",
                "유효하지 않은 client id",
                "앱 키가 올바르지 않습니다. 설정을 확인하세요. ",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("invalid_grant 에러: 인가 코드 만료/오류 예외가 발생한다. ")
    void error_invalidGrant() {
        performErrorTest(
                "invalid_grant",
                "expired",
                "인가 코드가 만료되었거나 잘못되었습니다. 다시 로그인해주세요. ",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("invalid_scope 에러: 잘못된 동의 항목 예외가 발생한다. ")
    void error_invalidScope() {
        performErrorTest(
                "invalid_scope",
                "wrong scope",
                "잘못된 동의 항목 ID입니다. ",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("misconfigured 에러: 플랫폼 설정 불일치 예외가 발생한다. ")
    void error_misconfigured() {
        performErrorTest(
                "misconfigured",
                "settings error",
                "플랫폼 설정이 올바르지 않습니다. 카카오 개발자 콘솔을 확인하세요. ",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("access_denied 에러: 사용자 취소 예외가 발생한다. ")
    void error_accessDenied() {
        performErrorTest(
                "access_denied",
                "user denied",
                "사용자가 로그인/동의를 취소했습니다.",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("server_error 에러: 카카오 서버 오류 예외가 발생한다. ")
    void error_serverError() {
        performErrorTest(
                "server_error",
                "internal",
                "카카오 서버 오류입니다. 잠시 후 다시 시도해주세요. ",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @Test
    @DisplayName("알 수 없는 에러 코드: 기본 에러 메시지 예외가 발생한다. ")
    void error_unknown() {
        performErrorTest(
                "error",
                "something odd",
                "카카오 토큰 요청 실패: error / something odd",
                HttpStatus.BAD_REQUEST
        );
    }

    @Test
    @DisplayName("에러 바디 JSON 파싱 실패 시 카카오 에러 파싱 예외가 발생한다. ")
    void error_parsingFailure() {
        commonExpect("any")
                .andRespond(withBadRequest()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("not-a-json"));

        assertThatThrownBy(() -> service.requestAccessToken("any"))
                .isInstanceOf(KakaoAuthException.class)
                .hasMessageContaining("카카오 에러 바디 파싱 실패");

        server.verify();
    }

    private ResponseActions commonExpect(String code) {
        String encoded = URLEncoder.encode(REDIRECT_URI, StandardCharsets.UTF_8);

        return server.expect(once(), requestTo(TOKEN_URL))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE))
                .andExpect(content().string(allOf(
                        containsString("grant_type=authorization_code"),
                        containsString("client_id="+CLIENT_ID),
                        containsString("redirect_uri="+encoded),
                        containsString("code="+code)
                )));
    }

    private void performErrorTest(
            String error,
            String description,
            String expectedMessage,
            HttpStatus status
    ) {
        String errBody = String.format("""
            {
              "error":"%s",
              "error_description":"%s"
            }
            """, error, description);

        DefaultResponseCreator responseCreator = status.is5xxServerError() ? withServerError() : withBadRequest();

        commonExpect("any")
                .andRespond(responseCreator
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(errBody));

        assertThatThrownBy(() -> service.requestAccessToken("any"))
                .isInstanceOf(KakaoAuthException.class)
                .hasMessageContaining(expectedMessage);

        server.verify();
    }
}
