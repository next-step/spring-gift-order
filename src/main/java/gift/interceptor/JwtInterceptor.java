package gift.interceptor;

import gift.auth.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final AuthErrorResponseHandler errorResponseHandler;

    public JwtInterceptor(AuthenticationService authenticationService,
                          AuthorizationService authorizationService,
                          AuthErrorResponseHandler errorResponseHandler) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.errorResponseHandler = errorResponseHandler;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        AuthenticationResult authResult = authenticationService.authenticate(request.getHeader("Authorization"));
        if (!authResult.isSuccess()) {
            errorResponseHandler.handleAuthenticationError(response, authResult.getErrorMessage());
            return false;
        }

        request.setAttribute("memberId", authResult.getMemberId());
        request.setAttribute("email", authResult.getEmail());
        request.setAttribute("role", authResult.getRole());

        AuthorizationResult authzResult = authorizationService.authorize(request.getRequestURI(), authResult.getRole());
        if (!authzResult.isSuccess()) {
            errorResponseHandler.handleAuthorizationError(response, authzResult.getErrorMessage());
            return false;
        }
        return true;
    }
}