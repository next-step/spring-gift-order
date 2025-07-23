package gift.config;

import gift.filter.JwtAuthFilter;
import gift.security.JwtProvider;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {

    private final JwtProvider jwtProvider;

    public FilterConfig(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtAuthFilter() {
        FilterRegistrationBean<JwtAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new JwtAuthFilter(jwtProvider));
        registrationBean.addUrlPatterns("/api/products/*", "/admin/products/*", "/api/wishes/*");
        return registrationBean;
    }
}
