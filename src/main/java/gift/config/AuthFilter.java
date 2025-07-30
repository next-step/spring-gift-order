package gift.config;

import gift.Jwt.TokenUtils;
import gift.config.Interceptor.AuthURl;
import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.List;

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

        boolean isPublic = AuthURl.PUBLIC_PATHS.contains(uri);
        boolean requiresAuth = AuthURl.AUTH_REQUIRED_PREFIXES.stream().anyMatch(uri::startsWith);

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
