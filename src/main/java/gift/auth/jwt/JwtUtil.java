package gift.auth.jwt;

import gift.entity.member.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {

    private static final long EXPIRATION_TIME = 1000 * 60 * 60;
    private final SecretKey secretKey;

    public JwtUtil(@Value("${jwt.secret}") String secretKeyString) {
        this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes());
    }

    public String generateToken(Member member) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + EXPIRATION_TIME);

        return Jwts.builder()
            .subject(String.valueOf(member.getProviderId()))
            .claim("memberId", member.getId())
            .claim("email", member.getEmail())
            .claim("nickname", member.getNickname())
            .claim("profileImage", member.getProfileImage())
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey)
            .compact();
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
}