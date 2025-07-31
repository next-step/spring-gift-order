package gift.config.Interceptor;

import gift.Jwt.TokenUtils;
import gift.entity.UserRole;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

public class RoleCheckInterceptor implements HandlerInterceptor {
    private final TokenUtils tokenUtils;

    public RoleCheckInterceptor(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod handlerMethod) {
            AdminOnly annotation = handlerMethod.getMethodAnnotation(AdminOnly.class);

            if (annotation != null) {
                String token = tokenUtils.extractToken(request.getHeader(HttpHeaders.AUTHORIZATION));
                tokenUtils.validateToken(token);
                if (!tokenUtils.hasRole(token, UserRole.ADMIN)) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.getWriter().write("관리자만 접근 가능합니다.");
                    return false;
                }
            }
        }
        return true;
    }
}
