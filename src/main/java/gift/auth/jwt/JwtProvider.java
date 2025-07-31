package gift.auth.jwt;

import gift.common.exception.InvalidTokenException;
import gift.common.exception.UnauthorizedException;
import io.jsonwebtoken.JwtException;
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
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException();
        }
        return header.substring(7);
    }

    public Map<String, Object> getClaimsFromToken(String token) {
        try {
            return jwtUtil.getClaims(token);
        } catch (JwtException e) {
            throw new InvalidTokenException();
        }
    }

}
