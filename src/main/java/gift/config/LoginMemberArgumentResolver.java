package gift.config;

import gift.annotation.LoginMember;
import gift.util.JwtUtil;
import gift.service.AuthService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public LoginMemberArgumentResolver(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        String authorization = webRequest.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid authorization header");
        }

        String token = authorization.substring(7);

        if (!jwtUtil.isTokenValid(token)) {
            throw new RuntimeException("Invalid token");
        }

        String email = jwtUtil.extractEmail(token);
        return authService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Member not found"));
    }
}