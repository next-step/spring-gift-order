package gift.wishPreProcess;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;
    private final AuthorizationInterceptor authorizationInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    public WebConfig(AuthenticationInterceptor authenticationInterceptor,
        AuthorizationInterceptor authorizationInterceptor,
        LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.authenticationInterceptor = authenticationInterceptor;
        this.authorizationInterceptor = authorizationInterceptor;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
            .addPathPatterns("/api/wishes/**")
            .addPathPatterns("/api/orders/**")
            .addPathPatterns("/managerHome/**")
            .order(1);
        registry.addInterceptor(authorizationInterceptor)
            .addPathPatterns("/managerHome/**")
            .order(1);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }
}
