package gift.service;

import gift.common.exception.InvalidTokenException;
import gift.domain.Role;
import gift.domain.User;
import gift.dto.user.UserInfo;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtTokenProvider {

    @Value("${spring.jwt.secret_key}")
    String secretKey;

    private static final String ACCESS_SUBJECT = "access";

    public String createToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(ACCESS_SUBJECT)
                .claims(createClaims(user))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + (30 * 60 * 1000L)))
                .signWith(getSignInKey())
                .compact();
    }

    public Claims validAccessToken(String token) {
        Claims claims = extractAllClaims(token);
        validateAccessClaims(claims);
        return claims;
    }

    public UserInfo getUserInfoFromClaims(Claims claims) {
        return new UserInfo(Long.valueOf((String) claims.get("id")), Role.valueOf((String) claims.get("role")));
    }

    private void validateAccessClaims(Claims claims) {
        if (!claims.getSubject().equals(ACCESS_SUBJECT)) {
            throw new InvalidTokenException();
        }

        String id = (String) claims.get("id");
        if (id == null) {
            throw new InvalidTokenException();
        }

        String role = (String) claims.get("role");
        if (role == null) {
            throw new InvalidTokenException();
        }
    }

    private Key getSignInKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    private Claims extractAllClaims(String token) {
        return Jwts
                .parser()
                .verifyWith((SecretKey) getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private Map<String, Object> createClaims(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", String.valueOf(user.getId()));
        map.put("role", user.getRole().name());
        return map;
    }
}
