package gift.config.Interceptor;

import gift.Jwt.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class UserCheckInterceptor implements HandlerInterceptor {
    private final TokenUtils tokenUtils;

    public UserCheckInterceptor(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            UserOnly annotation = handlerMethod.getMethodAnnotation(UserOnly.class);

            if (annotation != null) {
                String token = tokenUtils.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
                if (tokenUtils.extractUserRole(token) == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    return false;
                }
            }
        }
        return true;
    }

}
