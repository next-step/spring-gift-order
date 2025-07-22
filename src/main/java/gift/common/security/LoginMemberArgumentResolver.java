package gift.common.security;

import gift.common.security.exception.InvalidTokenException;
import gift.common.security.exception.MissingTokenException;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenProvider jwtTokenProvider;

    public LoginMemberArgumentResolver(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }


    @Override
    public Object resolveArgument(
        MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory
    ) throws Exception {

        // 어노테이션이 AuthenticatedMember에 붙어있지 않을 때
        if (parameter.getParameterType() != AuthenticatedMember.class) {
            throw new IllegalArgumentException();
        }

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String authorizationHeader = request.getHeader("Authorization");

        // 토큰이 잘못된 형식일 때
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new MissingTokenException();
        }

        String token = authorizationHeader.substring(7);

        // 토큰이 유효하지 않을 때
        if (!jwtTokenProvider.validateToken(token)) {
            throw new InvalidTokenException();
        }

        Claims claims = jwtTokenProvider.parseToken(token);
        Long id = Long.valueOf(claims.get("id", String.class));
        String name = claims.get("name", String.class);
        String email = claims.get("email", String.class);

        return new AuthenticatedMember(id, name, email);

    }
}
