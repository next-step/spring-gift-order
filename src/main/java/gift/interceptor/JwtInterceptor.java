package gift.interceptor;

import gift.auth.*;
import gift.entity.Member;
import gift.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    private final AuthenticationService authenticationService;
    private final AuthorizationService authorizationService;
    private final AuthErrorResponseHandler errorResponseHandler;
    private final MemberRepository memberRepository;

    public JwtInterceptor(AuthenticationService authenticationService,
                          AuthorizationService authorizationService,
                          AuthErrorResponseHandler errorResponseHandler,
                          MemberRepository memberRepository) {
        this.authenticationService = authenticationService;
        this.authorizationService = authorizationService;
        this.errorResponseHandler = errorResponseHandler;
        this.memberRepository = memberRepository;
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