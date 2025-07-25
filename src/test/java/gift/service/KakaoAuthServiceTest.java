package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.auth.KakaoProperties;
import gift.config.KakaoConfig;
import gift.dto.KakaoTokenResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(KakaoAuthService.class)
@Import({KakaoConfig.class, KakaoProperties.class})
class KakaoAuthServiceTest {

    @Autowired
    private KakaoAuthService kakaoAuthService;

    @Autowired
    private RestClient.Builder restClientBuilder;

    @Autowired
    private ObjectMapper objectMapper;

    private MockRestServiceServer mockServer;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
    }

    @Test
    @DisplayName("카카오 액세스 토큰 발급 테스트")
    void getAccessToken() throws JsonProcessingException {
        // given
        var authorizationCode = "test_auth_code";
        var expectedToken = new KakaoTokenResponse("bearer", "test_access_token", 86399, "test_refresh_token", 5184000, "talk_message");
        var responseBody = objectMapper.writeValueAsString(expectedToken);

        mockServer.expect(requestTo("https://kauth.kakao.com/oauth/token"))
            .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        // when
        var accessToken = kakaoAuthService.getAccessToken(authorizationCode);

        // then
        mockServer.verify();
        assertThat(accessToken).isEqualTo("test_access_token");
    }
}
