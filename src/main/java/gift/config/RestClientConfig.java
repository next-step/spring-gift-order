package gift.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    // 카카오 인증 API를 위한 RestClient 빈
    @Bean
    @Qualifier("kauthApiClient")
    public RestClient kauthApiClient() {
        return RestClient.builder()
                .baseUrl("https://kauth.kakao.com")
                .build();
    }

    // 카카오 서비스 API를 위한 RestClient 빈
    @Bean
    @Qualifier("kapiApiClient")
    public RestClient kapiApiClient() {
        return RestClient.builder()
                .baseUrl("https://kapi.kakao.com")
                .build();
    }
}