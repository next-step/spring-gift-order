package gift.resolver;

import gift.annotation.LoginMember;
import gift.dto.MemberResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.service.JwtProvider;
import gift.service.MemberService;
import io.jsonwebtoken.Claims;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public LoginMemberArgumentResolver(MemberService memberService, JwtProvider jwtProvider) {
        this.memberService = memberService;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class) &&
                parameter.getParameterType().equals(MemberResponse.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {

        String token = extractToken(webRequest);

        if (!jwtProvider.validateToken(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN);
        }
        Claims claims = jwtProvider.parseClaims(token);
        String email = claims.getSubject();
        return memberService.getByEmail(email);
    }

    private String extractToken(NativeWebRequest webRequest) {
        String authHeader = webRequest.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BusinessException(ErrorCode.MISSING_AUTHORIZATION_HEADER);
        }

        return authHeader.substring(7);
    }
}
