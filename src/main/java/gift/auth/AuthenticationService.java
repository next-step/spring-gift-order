package gift.auth;

import gift.util.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationService {
    private final JwtUtil jwtUtil;

    public AuthenticationService(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    public AuthenticationResult authenticate(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return AuthenticationResult.failure("Authorization header is missing or invalid");
        }

        String token = authHeader.substring(7);
        if (!jwtUtil.isTokenValid(token)) {
            return AuthenticationResult.failure("Invalid or expired token");
        }

        return AuthenticationResult.success(
                jwtUtil.extractMemberId(token),
                jwtUtil.extractEmail(token),
                jwtUtil.extractRole(token)
        );
    }
}
