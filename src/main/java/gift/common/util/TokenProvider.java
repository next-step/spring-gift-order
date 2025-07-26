package gift.common.util;

import gift.common.mapper.ProviderMapper;
import gift.common.model.error.TokenInfo;
import gift.entity.type.Provider;
import gift.entity.type.UserRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import io.jsonwebtoken.security.*;
import javax.crypto.SecretKey;
import java.time.Clock;
import java.time.Instant;
import java.util.Comparator;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class TokenProvider implements InitializingBean {
    private static final String AUTHORITIES_KEY = "auth";
    private static final Logger log = LoggerFactory.getLogger(TokenProvider.class);

    private final ProviderMapper providerMapper;
    private final String secret;
    private final Long expiration;
    private SecretKey secretKey;


    public TokenProvider(
        ProviderMapper providerMapper,
        @Value("${gift.jwt.secret}") @Valid String secret,
        @Value("${gift.jwt.expiration}") @Valid Long expiration
    ) {
        this.providerMapper = providerMapper;
        this.secret = secret;
        this.expiration = expiration;
    }

    @Override
    public void afterPropertiesSet() {
        if (secret == null || secret.isEmpty()) {
            throw new BeanInitializationException(
                "설정을 통해 올바른 JWT 비밀 키를 제공해야 합니다.: gift.jwt.secret=???"
            );
        }
        if (secret.length() < 32) {
            throw new BeanInitializationException(
                "설정을 통해 최소 32자 이상의 JWT 비밀 키를 제공해야 합니다.: gift.jwt.secret=???"
            );
        }
        if (expiration == null || expiration <= 0) {
            throw new BeanInitializationException(
                "설정을 통해 올바른 JWT 만료 기간을 제공해야 합니다.: gift.jwt.expiration=???"
            );
        }
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(Long userId, Set<UserRole> authorities) {
        Instant now = Instant.now(Clock.systemDefaultZone());
        Instant expiryDate = now.plusSeconds(expiration);

        String authoritiesString = authorities.stream()
                .map(UserRole::toString)
                .collect(Collectors.joining(","));

        return Jwts.builder()
                .subject(userId.toString())
                .claim(AUTHORITIES_KEY, authoritiesString)
                .issuedAt(Date.from(now))
                .issuer(providerMapper.toIssuer(Provider.EMAIL)) // 이 서버에서 발급한 토큰임을 나타냅니다.
                .expiration(Date.from(expiryDate))
                .signWith(this.secretKey)
                .compact();
    }

    public TokenInfo getTokenInfo(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        // Extract  ID from claims
        String IdString = claims.getSubject();
        if (IdString == null || IdString.isEmpty()) {
            return null;
        }

        // Extract authorities from claims
        String authoritiesString = claims.get(AUTHORITIES_KEY, String.class);
        UserRole highestRole =  Stream.of(authoritiesString.split(","))
                .map(UserRole::fromString)
                .max(Comparator.comparing(UserRole::getPriority))
                .orElse(UserRole.ROLE_GUEST);

        // Extract provider from claims
        String issuer = claims.getIssuer();
        Provider provider = providerMapper.toProvider(issuer);
        return new TokenInfo(IdString, highestRole, provider);
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(this.secretKey)
                .build()
                .parse(token);
            return true;
        } catch (SecurityException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (MalformedJwtException e) {
            log.warn("잘못된 JWT 토큰입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (Exception e) {
            log.warn("유효하지 않은 JWT 토큰입니다.");
        }
        return false;
    }
}
