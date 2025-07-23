package gift.jwt;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final AuthenticatedArgumentResolver authenticatedArgumentResolver;

    public WebConfiguration(AuthenticatedArgumentResolver authenticatedArgumentResolver) {
        this.authenticatedArgumentResolver = authenticatedArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedArgumentResolver);
    }
}
