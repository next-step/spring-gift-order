package gift.Jwt;

import gift.Entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;


@Component
public class JwtUtil {

    private String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";

    // 토큰 생성
    public String createToken(Member member) {
        return Jwts.builder()
                .setSubject(member.getNickname())
                .claim("name", member.getName())
                .claim("email", member.getEmail())
                .claim("address", member.getAddress())
                .claim("role", member.getRole())
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .compact();
    }

    /*
    암호화 했던 토큰을 다시 되돌려서 확인하도록 함
    -> 암호화가 아니라 JWT는 서명을 사용함
    암호화는 내용을 숨기는 것이고,
    서명은 내용을 보호하는 것이다. 즉 변조의 여부만 확인이 가능하도록 한다.
    parseToken은 토큰이 유효한지만 확인하고 내부 정보(Claims)를 꺼내는 과정이다.
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                // SecretKey를 통해 변조되었는지 확인하고, 변조되지 않았다면 토큰에서 정보를 가져오는 형태
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes()))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
