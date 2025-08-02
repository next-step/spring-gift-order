package gift.config;

import gift.interceptor.CustomAuthInterceptor;
import gift.properties.Properties;
import gift.resolver.LoginMemberArgumentResolver;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CustomConfig implements WebMvcConfigurer {

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final CustomAuthInterceptor customAuthInterceptor;
    private final Properties properties;

    public CustomConfig(
            LoginMemberArgumentResolver loginMemberArgumentResolver,
            CustomAuthInterceptor customAuthInterceptor,
            Properties properties) {
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.customAuthInterceptor = customAuthInterceptor;
        this.properties = properties;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customAuthInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/api/members/register", "/api/members/login",
                        "/admin/boards/**", "/kakao/login", "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(properties.getCorsAllowedOrigin())
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .exposedHeaders(HttpHeaders.LOCATION);
    }
}