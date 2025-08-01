package gift.filter;

import gift.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;

public abstract class JwtFilter extends OncePerRequestFilter {

    protected final JwtUtil jwtUtil;

    protected JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    protected abstract boolean shouldFilter(HttpServletRequest httpServletRequest);

    protected abstract String resolveToken(HttpServletRequest httpServletRequest);

    protected abstract void writeError(HttpServletResponse httpServletResponse, String message)
            throws IOException;

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest httpServletRequest,
            @NotNull HttpServletResponse httpServletResponse,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        if (HttpMethod.OPTIONS.name().equalsIgnoreCase(httpServletRequest.getMethod())) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        if (!shouldFilter(httpServletRequest)) {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
            return;
        }

        String token = resolveToken(httpServletRequest);
        if (token == null || token.isBlank()) {
            writeError(httpServletResponse, "유효한 토큰이 필요합니다.");
            return;
        }

        try {
            Jws<Claims> jws = jwtUtil.parseToken(token);
            Claims claims = jws.getPayload();

            httpServletRequest.setAttribute("authClaims", claims);

            Object rawId = claims.get("memberId");
            Long memberId = null;
            if (rawId instanceof Integer i) {
                memberId = i.longValue();
            } else {
                throw new JwtException("memberId 클레임이 올바른 숫자 타입이 아닙니다: " + rawId);
            }

            httpServletRequest.setAttribute("memberId", memberId);
            httpServletRequest.setAttribute("kakaoAccessToken", claims.get("kakaoAccessToken"));

            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } catch (JwtException ex) {
            writeError(httpServletResponse, "유효하지 않은 토큰입니다.");
        }
    }
}
