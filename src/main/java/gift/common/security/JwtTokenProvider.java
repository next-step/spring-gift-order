package gift.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenProvider {

    private final SecretKey secretKey;

    private final long accessTokenValidityTime;

    public JwtTokenProvider(
        @Value("${jwt.secret}") String secretKey,
        @Value("${jwt.access-token-validity}") long accessTokenValidityTime) {
        this.secretKey = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.accessTokenValidityTime = accessTokenValidityTime;
    }

    /**
     * 토큰 생성 메서드
     *
     * @param userId
     * @param username
     * @param userEmail
     * @return 생성된 토큰
     */
    public String generateToken(Long userId, String username, String userEmail) {
        return Jwts.builder()
            .claim("id", userId.toString())
            .claim("name", username)
            .claim("email", userEmail)
            .expiration(Date.from(Instant.now().plusSeconds(accessTokenValidityTime)))
            .signWith(secretKey)
            .compact();
    }

    /**
     * JWT 검증 메서드
     *
     * @param token 검증할 토큰
     * @return 검증 성공 여부
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 토큰 정보 추출 메서드
     *
     * @param token 추출할 토큰
     * @return 토큰에 담긴 정보 Map
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }


    /**
     * 토큰의 만료 여부 체크 메서드
     *
     * @param token 만료 여부를 체크할 토큰
     * @return 만료 여부 (만료시 true)
     */
    public boolean isTokenExpired(String token) {
        return parseToken(token).getExpiration().before(new Date());
    }

}
