package gift.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Duration;
import java.util.Date;

public class JwtUtil {

    private static final String SECRET_KEY = System.getenv().getOrDefault(
            "JWT_SECRET", "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E="
    );

    private static final Duration EXPIRATION = Duration.ofMillis(
            Long.parseLong(System.getenv().getOrDefault("JWT_EXPIRATION", "3600000"))
    );

    private static final Key KEY = Keys.hmacShaKeyFor(SECRET_KEY.getBytes());

    public static String generateToken(Long memberId, String email) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .subject(String.valueOf(memberId))
                .claim("email", email)
                .expiration(new Date(now + EXPIRATION.toMillis()))
                .signWith(KEY)
                .compact();
    }

    public static boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) KEY).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static Claims getClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) KEY).build().parseSignedClaims(token).getPayload();
    }
}
