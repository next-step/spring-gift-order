package gift.config;

import gift.interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final AuthenticationInterceptor authenticationInterceptor;

    public WebConfig(AuthenticationInterceptor authenticationInterceptor) {
        this.authenticationInterceptor = authenticationInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(
                        "/api/products/**",
                        "/api/wishes/**",
                        "/api/orders/**",
                        "/admin/**",
                        "/members/products/**",
                        "/members/wishes/**"
                );
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 1. 모든 API 경로에 대해 CORS 설정을 적용합니다.
                .allowedOrigins("http://3.39.239.230") // 2. 허용할 클라이언트의 출처(Origin)를 명시합니다.
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH",
                        "OPTIONS") // 3. 허용할 HTTP 메서드를 지정합니다.
                .allowedHeaders("*") // 4. 허용할 모든 요청 헤더를 지정합니다.
                .allowCredentials(true) // 5. 쿠키 등 인증 정보를 포함한 요청을 허용합니다.
                .maxAge(3600); // 6. Pre-flight 요청의 캐시 시간을 설정합니다. (단위: 초)
    }
}
