package gift.config;

import gift.interceptor.AdminCheckInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AdminCheckInterceptor adminCheckInterceptor;
    private final LoginMemberArgumentResolver resolver;
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    public WebConfig(AdminCheckInterceptor adminCheckInterceptor, LoginMemberArgumentResolver resolver, LoginUserArgumentResolver loginUserArgumentResolver) {
        this.adminCheckInterceptor = adminCheckInterceptor;
        this.resolver = resolver;
        this.loginUserArgumentResolver = loginUserArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminCheckInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(resolver);
        resolvers.add(loginUserArgumentResolver);
    }

    public static final String[] ALLOWED_METHOD_NAMES = {
            "GET", "POST", "PUT", "DELETE", "OPTIONS"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")  // 모든 Origin 허용
                .allowedMethods(ALLOWED_METHOD_NAMES)
                .exposedHeaders(HttpHeaders.LOCATION) // Location 헤더 노출
                .allowCredentials(false);
    }
}
