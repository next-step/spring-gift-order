package gift.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class KakaoClientConfig {

  @Bean
  @Qualifier("kakaoOauthClient")
  public RestClient kakaoOauthClient() {
    return RestClient.builder()
        .baseUrl("https://kauth.kakao.com")
        .build();
  }

  @Bean
  @Qualifier("kakaoApiClient")
  public RestClient kakaoApiClient() {
    return RestClient.builder()
        .baseUrl("https://kapi.kakao.com")
        .build();
  }
}
