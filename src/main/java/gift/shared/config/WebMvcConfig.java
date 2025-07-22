package gift.shared.config;

import gift.shared.filter.LoginMemberArgumentHandler;
import gift.shared.token.service.TokenService;
import gift.user.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class WebMvcConfig implements WebMvcConfigurer {
    private final UserService userService;
    private final TokenService tokenService;

    public WebMvcConfig(UserService userService, TokenService tokenService) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentHandler(userService, tokenService));
    }
}
