package gift.wishPreProcess;

import gift.auth.JwtTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthorizationInterceptor implements HandlerInterceptor {

    private static final String USER_ROLE = "USER";
    private static final String MANAGER_ROLE = "MANAGER";

    private final JwtTokenHandler jwtTokenHandler;

    private final String tokenType = "Bearer ";

    public AuthorizationInterceptor(final JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws Exception {

        String token = request.getHeader(tokenType);
        String userRole = jwtTokenHandler.getUserRoleFromToken(token);

        if (userRole.equals(USER_ROLE)) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Not a Manager\"}");
            return false;
        }
        return true;
    }
}
