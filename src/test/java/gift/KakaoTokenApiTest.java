package gift;


import static org.assertj.core.api.Assertions.assertThat;

import gift.config.KakaoProperties;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DisplayName("Kakao Token 발급 API 테스트")
public class KakaoTokenApiTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @Autowired
    private KakaoProperties kakaoProperties;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
            .baseUrl("http://localhost:" + port)
            .build();
    }

    @Test
    void 카카오로그인_리다이렉트_성공하면_302() {
        String kakaoLoginPath = "/kakao/login";
        String expectedRedirectUrl = UriComponentsBuilder.fromUriString("https://kauth.kakao.com")
            .path("/oauth/authorize")
            .queryParam("response_type", "code")
            .queryParam("client_id", kakaoProperties.getClientId())
            .queryParam("redirect_uri", kakaoProperties.getRedirectUri())
            .build().toUriString();

        var response = restClient.get()
            .uri(kakaoLoginPath)
            .retrieve()
            .toBodilessEntity();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FOUND);

        URI locationHeader = response.getHeaders().getLocation();
        assertThat(locationHeader).isNotNull();
    }

}
