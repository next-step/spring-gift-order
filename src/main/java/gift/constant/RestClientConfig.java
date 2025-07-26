package gift.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${custom.kakao-auth-server}")
    private String kakao_auth_server;

    @Bean
    public RestClient kakaoRestClient() {
        return RestClient.builder()
            .baseUrl(kakao_auth_server)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }

}
