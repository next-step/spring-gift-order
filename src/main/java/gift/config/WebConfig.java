package gift.config;

import gift.Jwt.TokenUtils;
import gift.config.Interceptor.RoleCheckInterceptor;
import gift.config.Interceptor.TemporaryUserCheckInterceptor;
import gift.config.Interceptor.UserCheckInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class WebConfig implements WebMvcConfigurer {

    private final TokenUtils tokenUtils;

    public WebConfig(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginUserArgumentResolver(tokenUtils));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleCheckInterceptor(tokenUtils))
                .addPathPatterns("/**");

        registry.addInterceptor(new UserCheckInterceptor(tokenUtils))
                .addPathPatterns("/**");

        registry.addInterceptor((new TemporaryUserCheckInterceptor(tokenUtils)))
                .addPathPatterns("/**");
    }

}
