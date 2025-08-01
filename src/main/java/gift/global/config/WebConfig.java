package gift.global.config;

import gift.global.interceptor.LoginCheckInterceptor;
import gift.global.resolver.LoginMemberArgumentResolver;
import gift.member.auth.JwtProvider;
import gift.member.repository.MemberRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final LoginCheckInterceptor loginCheckInterceptor;
    private final JwtProvider jwtProvider;
    private final MemberRepository memberRepository;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    public WebConfig(LoginCheckInterceptor loginCheckInterceptor,
                     JwtProvider jwtProvider,
                     MemberRepository memberRepository,
                     LoginMemberArgumentResolver loginMemberArgumentResolver) {
        this.loginCheckInterceptor = loginCheckInterceptor;
        this.jwtProvider = jwtProvider;
        this.memberRepository = memberRepository;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
    }

    @Bean
    public HiddenHttpMethodFilter hiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginCheckInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/api/members/register",
                        "/api/members/login",
                        "/api/oauth/kakao/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해
                .allowedOrigins("*") // 전부 허용
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 필요한 HTTP 메서드 허용
                .allowCredentials(false) // jwt이기 때문에 비허용
                .allowedHeaders("*") // 모든 헤더 허용
                .maxAge(3600); // preflight 요청 캐시 시간
    }
}
