package gift.config;

import gift.member.service.MemberService;
import gift.resolver.LoginMemberArgumentResolver;
import gift.token.service.TokenProvider;
import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TokenProvider tokenProvider;
    private final MemberService memberService;

    public WebConfig(TokenProvider tokenProvider, MemberService memberService) {
        this.tokenProvider = tokenProvider;
        this.memberService = memberService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        PageableHandlerMethodArgumentResolver pageableResolver = new PageableHandlerMethodArgumentResolver();
        pageableResolver.setMaxPageSize(100);
        pageableResolver.setFallbackPageable(PageRequest.of(0, 20));

        resolvers.add(pageableResolver);
        resolvers.add(new LoginMemberArgumentResolver(tokenProvider, memberService));
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .combine(new CorsConfiguration().applyPermitDefaultValues())
                .exposedHeaders("Location");
    }
}
