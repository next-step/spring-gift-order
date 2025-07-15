package gift.authorization.service;

import gift.member.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
public class JwtProvider {

    private final Key key;

    public JwtProvider(@Value("${jwt.secret}") String secretKey) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
    }

    public String createToken(Long memberId, String name, String email, Role role) {
        return Jwts.builder()
                .subject(memberId.toString())
                .claim("name", name)
                .claim("email", email)
                .claim("role", role)
                .signWith(key)
                .compact();
    }

    public Long getMemberId(String token) {
        return Long.valueOf(Jwts.parser()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject());
    }
}
