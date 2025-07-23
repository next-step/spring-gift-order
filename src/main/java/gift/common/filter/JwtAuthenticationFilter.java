package gift.common.filter;

import gift.common.exception.InvalidAccessTokenException;
import gift.common.exception.InvalidTokenException;
import gift.dto.user.UserInfo;
import gift.service.JwtTokenProvider;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Component
@Order(2)
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final String AUTH_HEADER = "Authorization";
    private static final String HEADER_PREFIX = "Bearer ";
    private static final String COOKIE_NAME = "accessToken";
    private static final AntPathMatcher MATCHER = new AntPathMatcher();

    private static final Map<String, Set<HttpMethod>> EXCLUDED_PATHS = Map.of(
            "/api/users/register", Set.of(HttpMethod.POST),
            "/api/users/login", Set.of(HttpMethod.POST),
            "/admin/login", Set.of(HttpMethod.GET, HttpMethod.POST),
            "/api/products", Set.of(HttpMethod.GET),

            "/favicon.ico", Set.of(HttpMethod.GET),
            "/product-form.css", Set.of(HttpMethod.GET),
            "/product-list.css", Set.of(HttpMethod.GET),

            "/h2-console/**", Set.of(HttpMethod.GET, HttpMethod.POST)
    );

    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        String requestURI = request.getRequestURI();

        Optional<String> match = EXCLUDED_PATHS.keySet().stream().filter(pattern -> MATCHER.match(pattern, requestURI)).findFirst();
        if (match.isPresent()) {
            Set<HttpMethod> methods = EXCLUDED_PATHS.get(match.get());
            if (methods.contains(method)) {
                filterChain.doFilter(request, response);
                return;
            }
        }

        try {
            String jwt = null;
            String authHeader = request.getHeader(AUTH_HEADER);
            if (authHeader != null) {
                authHeader = authHeader.trim();
            }

            if (authHeader != null && authHeader.startsWith(HEADER_PREFIX)) {
                jwt = authHeader.substring(HEADER_PREFIX.length());
            }

            if (authHeader == null) {
                if (request.getCookies().length == 0) {
                    throw new InvalidAccessTokenException();
                }
                for (Cookie cookie : request.getCookies()) {
                    if (cookie.getName().equals(COOKIE_NAME)) {
                        String val = URLDecoder.decode(cookie.getValue(), StandardCharsets.UTF_8);
                        if (!val.startsWith(HEADER_PREFIX)) {
                            throw new InvalidTokenException();
                        }
                        jwt = val.substring(HEADER_PREFIX.length());
                        break;
                    }
                }
            }

            if (jwt == null) {
                throw new InvalidAccessTokenException();
            }

            Claims claims = jwtTokenProvider.validAccessToken(jwt);
            UserInfo userInfo = jwtTokenProvider.getUserInfoFromClaims(claims);
            request.setAttribute("userInfo", userInfo);

        } catch (RuntimeException e) {
            throw new InvalidTokenException(e);
        }

        filterChain.doFilter(request, response);
    }

}
