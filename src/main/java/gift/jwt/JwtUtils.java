package gift.jwt;

import org.springframework.stereotype.Component;

@Component
public class JwtUtils {
    private final JwtTokenProvider jwtTokenProvider; // JwtTokenProvider를 주입받는다고 가정

    public JwtUtils(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    public String extractPureToken(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            return authorizationHeader.replace("Bearer ", "");
        }
        return null;
    }

}