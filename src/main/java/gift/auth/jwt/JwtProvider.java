package gift.auth.jwt;

import gift.common.exception.InvalidTokenException;
import gift.common.exception.UnauthorizedException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final JwtUtil jwtUtil;

    public JwtProvider(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public String extractToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            throw new UnauthorizedException();
        }

        for (Cookie cookie : cookies) {
            if ("access_token".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        throw new UnauthorizedException();
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        try {
            return jwtUtil.getClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

}
