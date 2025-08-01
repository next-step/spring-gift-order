package gift.auth.jwt;

import gift.common.exception.core.CustomException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;

@Component
public class JwtFilter implements Filter {

    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/css",
        "/images",
        "/api/auth"
    );
    private final JwtProvider jwtProvider;

    public JwtFilter(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            if (isExcludedPath(httpRequest.getRequestURI())) {
                chain.doFilter(request, response);
                return;
            }

            Long memberId = extractMemberIdFromToken(httpRequest);
            httpRequest.setAttribute("memberId", memberId);

            chain.doFilter(request, response);

        } catch (CustomException e) {
            sendErrorResponse(httpResponse, e.getStatus(), e.getMessage());
        }
    }


    private boolean isExcludedPath(String path) {
        if ("/".equals(path)) {
            return true;
        }

        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private Long extractMemberIdFromToken(HttpServletRequest request) {
        String token = jwtProvider.extractToken(request);
        Map<String, Object> claims = jwtProvider.getClaimsFromToken(token);

        return ((Number) claims.get("memberId")).longValue();
    }

    private void sendErrorResponse(HttpServletResponse response, HttpStatusCode statusCode,
        String message)
        throws IOException {
        response.setStatus(statusCode.value());
        response.setContentType("application/json; charset=UTF-8");

        String jsonResponse = String.format("""
            {
              "status": %d,
              "message": "%s",
              "data": null
            }
            """, statusCode.value(), message);

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
