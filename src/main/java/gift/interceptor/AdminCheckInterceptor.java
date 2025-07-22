package gift.interceptor;

import gift.config.JwtProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.Cookie;

@Component
public class AdminCheckInterceptor implements HandlerInterceptor {

    private final JwtProvider jwtProvider;

    public AdminCheckInterceptor(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String token = getTokenFromCookie(request);
        if (token == null) {
            response.sendRedirect("/user/login");
            return false;
        }


        String role = jwtProvider.getRole(token);

        if (!"ADMIN".equals(role)) {
            response.sendRedirect("/error/403");
            return false;
        }

        return true;
    }

    private String getTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
