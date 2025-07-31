package gift.shared.filter;

import gift.shared.annotation.KakaoUser;
import gift.shared.exception.token.NoTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static gift.shared.token.status.TokenStatus.*;

@Component
public class KakaoLoginMemberArgumentHandler implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(KakaoUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        HttpServletRequest req = webRequest.getNativeRequest(HttpServletRequest.class);
        if(req != null){
            final String authorization = req.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null) {
                return null;
            }
            if(!authorization.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(INVALID_TOKEN_TYPE);
            }
            return authorization.substring(7);
        }
        return new NoTokenException(NO_TOKEN.getMessage());
    }
}
