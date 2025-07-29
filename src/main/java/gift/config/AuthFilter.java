package gift.config;

import gift.Jwt.TokenUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;

public class AuthFilter implements Filter {

    private final TokenUtils tokenUtils;

    public AuthFilter(TokenUtils tokenUtils) {
        this.tokenUtils = tokenUtils;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();

        boolean isPublic = uri.equals("/api/users/register") || uri.equals("/api/users/login");

        boolean requiresAuth = uri.startsWith("/wish/") || uri.startsWith("/admin/products") || uri.startsWith("/api/products") || uri.startsWith("/api/wish") || uri.startsWith("/api/orders") || uri.startsWith("/api/options") || uri.startsWith("/api/users");

        if (isPublic || !requiresAuth) {
            chain.doFilter(request, response);
            return;
        }

        String authHeader = httpRequest.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        try {
            String token = tokenUtils.extractToken(authHeader);
            Claims claims = tokenUtils.getClaims(token);
            httpRequest.setAttribute("claims", claims);
            chain.doFilter(request, response);
        } catch (Exception e) {
            httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }


}
