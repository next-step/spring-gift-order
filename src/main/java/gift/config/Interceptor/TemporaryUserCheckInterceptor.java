package gift.config.Interceptor;

import gift.Jwt.TokenUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class TemporaryUserCheckInterceptor implements HandlerInterceptor {

    private final TokenUtils tokenUtils;

    public TemporaryUserCheckInterceptor(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            TemporaryOnly annotation = handlerMethod.getMethodAnnotation(TemporaryOnly.class);

            if (annotation != null) {
                String token = tokenUtils.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
                if (tokenUtils.extractUserRole(token) == null) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("임시 사용자만 접근 가능합니다.");
                    return false;
                }
            }
        }
        return true;
    }
}
