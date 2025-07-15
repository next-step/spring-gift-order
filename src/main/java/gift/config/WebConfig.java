package gift.config;

import gift.resolver.LoginMemberArgumentResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final List<HandlerMethodArgumentResolver> resolvers;

    public WebConfig(List<HandlerMethodArgumentResolver> resolvers) {
        this.resolvers = resolvers;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.addAll(this.resolvers);
    }
}
