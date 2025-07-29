package gift.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import org.springframework.util.StringUtils;

public class JwtUtil {

    private static final String SECRET_KEY = System.getenv().getOrDefault(
        "JWT_SECRET", "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="
    );

    private static final long EXPIRATION_TIME = Long.parseLong(
        System.getenv().getOrDefault("JWT_EXPIRATION", "3600000")
    );

    private static final SecretKey KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    private static final String BEARER_PREFIX = "Bearer ";

    public static String generateToken(Long memberId, String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
            .subject(String.valueOf(memberId))
            .claim("email", email)
            .expiration(new Date(now + EXPIRATION_TIME))
            .signWith(KEY)
            .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Claims getClaims(String token) {
        return Jwts.parser().verifyWith(KEY).build().parseSignedClaims(token).getPayload();
    }

    public static String getAccessTokenFromHeader(String header) {
        if (StringUtils.hasText(header) && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        throw new IllegalArgumentException("유효하지 않은 토큰 정보입니다.");
    }
}
