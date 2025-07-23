package gift.config;

import gift.Jwt.TokenUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final TokenUtils tokenUtils;

    public LoginUserArgumentResolver(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class) && parameter.getParameterType().equals(String.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        String authHeader = webRequest.getHeader(HttpHeaders.AUTHORIZATION);

        String token = tokenUtils.extractToken(authHeader);
        tokenUtils.validateToken(token);

        return tokenUtils.extractEmail(token);
    }
}
