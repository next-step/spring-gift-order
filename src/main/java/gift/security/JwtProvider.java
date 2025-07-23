package gift.security;

import gift.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {

    private static final long EXPIRE_TIME = 1000 * 60 * 30;

    private final SecretKey secretKey;

    public JwtProvider(@Value("${jwt.secret.key}") String secretKey) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String generateToken(User user) {
        Date now = new Date();
        Date expireDate = new Date(now.getTime() + EXPIRE_TIME);

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("email", user.email())
                .expiration(expireDate)
                .signWith(secretKey)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Long getUserId(String token) {
        return Long.parseLong(getClaims(token).getSubject());
    }

    public boolean isTokenExpired(String token) {
        Date expiration = getClaims(token).getExpiration();
        return expiration.before(new Date());
    }

}
