package gift.wishPreProcess;

import gift.auth.JwtTokenHandler;
import gift.service.MemberServiceImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtTokenHandler jwtTokenHandler;
    private final MemberServiceImpl userServiceImpl;

    public LoginMemberArgumentResolver(JwtTokenHandler jwtTokenHandler,
        MemberServiceImpl userServiceImpl) {
        this.jwtTokenHandler = jwtTokenHandler;
        this.userServiceImpl = userServiceImpl;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
        ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);

        String authHeader = request.getHeader("Authorization");
        authHeader = authHeader.substring(7);
        return jwtTokenHandler.getEmailFromToken(authHeader);
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class)
            && parameter.getParameterType().equals(String.class);
    }
}
