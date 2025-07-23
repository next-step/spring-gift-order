package gift.wishPreProcess;


import gift.auth.JwtTokenHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtTokenHandler jwtTokenHandler;

    private final String tokenType = "Bearer ";

    public AuthenticationInterceptor(final JwtTokenHandler jwtTokenHandler) {
        this.jwtTokenHandler = jwtTokenHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
        Object handler) throws IOException {

        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith(tokenType)) {
            token = token.substring(tokenType.length());
        }
        if (!jwtTokenHandler.verifyToken(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("{\"error\":\"Invalid token\"}");
            return false;
        }
        return true;
    }
}
