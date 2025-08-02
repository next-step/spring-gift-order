package gift.common.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class CookieToAttributeInterceptor implements HandlerInterceptor {
    private static final String COOKIE_TOKEN_HEADER = "access-token";

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        if (request.getCookies() == null) {
            return true; // 쿠키가 없으면 그냥 통과
        }

        for (Cookie cookie : request.getCookies()) {
            // access-token을 찾아서 Authorization 헤더로 설정
            if (COOKIE_TOKEN_HEADER.equals(cookie.getName())) {
                String token = cookie.getValue();
                if (token != null && !token.isEmpty()) {
                    String authHeader = BEARER_PREFIX + token;
                    request.setAttribute(AUTHORIZATION_HEADER, authHeader);
                }
            }
        }
        return true;
    }
}
