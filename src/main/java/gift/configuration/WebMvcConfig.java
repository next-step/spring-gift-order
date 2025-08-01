package gift.configuration;

import gift.resolver.TokenResolver;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final TokenResolver tokenResolver;

    public WebMvcConfig(TokenResolver tokenResolver) {
        this.tokenResolver = tokenResolver;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(tokenResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")         // 모든 엔드포인트
                .allowedOrigins("*")       // 모든 출처 허용
                .allowedMethods("*")       // 모든 HTTP 메서드 허용 (GET, POST, PUT, DELETE…)
                .allowedHeaders("*")       // 모든 헤더 허용
                .exposedHeaders(HttpHeaders.LOCATION) // LOCATION 헤더 공개
                .allowCredentials(false)   // 자격증명(쿠키, 인증정보) 불필요 시 false
                .maxAge(3600);             // pre-flight 응답 캐시 시간 (초)
    }
}
