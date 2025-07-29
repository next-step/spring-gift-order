package gift.configuration;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    // 카카오 인증 관련 API 요청 템플릿
    @Bean
    public RestTemplate kakaoAuthRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri("https://kauth.kakao.com")
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();
    }

    // 카카오 토큰으로 불러오는 API 요청 템플릿
    @Bean
    public RestTemplate kakaoKapiRestTemplate(RestTemplateBuilder builder) {
        return builder
                .rootUri("https://kapi.kakao.com")
                .connectTimeout(Duration.ofSeconds(3))
                .readTimeout(Duration.ofSeconds(3))
                .build();
    }
}
