package gift.constant;

import gift.kakao.KakaoMessageInterface;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class KakaoClientConfig {

    @Value("${custom.kakao-auth-server}")
    private String kakao_auth_server;

    @Value("${custom.kakao-sendTo-myself}")
    private String kakao_self_message;

    @Bean
    public RestClient kakaoAuthRequestClient() {
        return RestClient.builder()
            .baseUrl(kakao_auth_server)
            .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
            .build();
    }
    @Bean
    public RestClient.Builder kakaoMessageRequestClient() {
        return RestClient.builder()
            .baseUrl(kakao_self_message)
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    }
    @Bean
    public KakaoMessageInterface createKakaoMessageClient(RestClient kakaoRestClient) {

        return HttpServiceProxyFactory
            .builder()
            .exchangeAdapter(RestClientAdapter.create(kakaoRestClient))
            .build()
            .createClient(KakaoMessageInterface.class);
    }
}
