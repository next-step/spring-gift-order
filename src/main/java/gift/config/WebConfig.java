package gift.config;

import gift.handler.JwtAccessTokenResolver;
import gift.handler.JwtEmailResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final JwtEmailResolver jwtEmailResolver;
    private final JwtAccessTokenResolver accessTokenResolver;

    public WebConfig(JwtEmailResolver jwtEmailResolver, JwtAccessTokenResolver accessTokenResolver) {
        this.jwtEmailResolver = jwtEmailResolver;
        this.accessTokenResolver = accessTokenResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(jwtEmailResolver);
        resolvers.add(accessTokenResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins("http://localhost:3000")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type")
                .allowCredentials(true);
    }
}
