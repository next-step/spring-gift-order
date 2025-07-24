package gift.auth.jwt;

import gift.common.code.CustomResponseCode;
import gift.common.exception.CustomException;
import gift.entity.Member;
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
import org.springframework.stereotype.Component;

@Component
public class JwtFilter implements Filter {

    private static final Set<String> EXCLUDED_PATHS = Set.of(
        "/api/auth",
        "/h2-console",
        "/admin/products",
        "/css"
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

            Member member = extractMemberFromToken(httpRequest);
            httpRequest.setAttribute("member", member);

            chain.doFilter(request, response);

        } catch (CustomException e) {
            sendErrorResponse(httpResponse, e.getErrorCode());
        }
    }


    private boolean isExcludedPath(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private Member extractMemberFromToken(HttpServletRequest request) {
        String token = jwtProvider.extractToken(request);
        Map<String, Object> claims = jwtProvider.getClaimsFromToken(token);

        Long providerId = Long.valueOf((String) claims.get("sub"));
        Long memberId = ((Number) claims.get("memberId")).longValue();
        String email = (String) claims.get("email");
        String nickname = (String) claims.get("nickname");
        String profileImage = (String) claims.get("profileImage");

        return new Member(memberId, providerId, email, nickname, profileImage);
    }

    private void sendErrorResponse(HttpServletResponse response, CustomResponseCode code)
        throws IOException {
        response.setStatus(code.getCode());
        response.setContentType("application/json; charset=UTF-8");

        String jsonResponse = String.format("""
            {
              "status": %d,
              "message": "%s",
              "data": null
            }
            """, code.getCode(), code.getMessage());

        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}
