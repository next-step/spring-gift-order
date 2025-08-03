package gift.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private static final long MAX_AGE_SECONDS = 1800L;

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    public WebConfig(LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        // 주입받은 ArgumentResolver를 Spring의 Argument Resolver 목록에 등록
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOrigins("http://localhost:3000")
            .allowedMethods(
                HttpMethod.GET.name(),
                HttpMethod.POST.name(),
                HttpMethod.PUT.name(),
                HttpMethod.DELETE.name(),
                HttpMethod.HEAD.name(),
                HttpMethod.OPTIONS.name())
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(MAX_AGE_SECONDS);
    }
}