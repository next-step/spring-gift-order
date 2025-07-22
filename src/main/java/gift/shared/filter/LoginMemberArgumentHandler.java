package gift.shared.filter;

import gift.shared.annotation.AuthUser;
import gift.shared.exception.token.NoTokenException;
import gift.shared.token.service.TokenService;
import gift.user.service.UserService;
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
public class LoginMemberArgumentHandler implements HandlerMethodArgumentResolver {
    private final UserService userService;
    private final TokenService tokenService;

    public LoginMemberArgumentHandler(
            UserService userService,
            TokenService tokenService
    ) {
        this.userService = userService;
        this.tokenService = tokenService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(AuthUser.class);
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
            final String token = authorization.substring(7);
            if(tokenService.isTokenExpired(token)){
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(TOKEN_EXPIRED);
            }
            Long userId = tokenService.extractId(token);
            return userService.getUserInfo(userId);
        }
        return new NoTokenException(NO_TOKEN.getErrorMessage());
    }
}
