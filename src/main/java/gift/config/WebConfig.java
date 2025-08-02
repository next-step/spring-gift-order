package gift.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

  private final LoginMemberArgumentResolver loginMemberArgumentResolver;

  public WebConfig(LoginMemberArgumentResolver loginMemberArgumentResolver) {
    this.loginMemberArgumentResolver = loginMemberArgumentResolver;
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    resolvers.add(loginMemberArgumentResolver);
  }

  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry.addMapping("/**") //모든 경로
        .allowedOrigins("http://localhost:3000") //해당 origin만 허용
        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")//모두 허용
        .allowCredentials(true);
  }
}
