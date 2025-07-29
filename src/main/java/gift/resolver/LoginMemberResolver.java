package gift.resolver;

import gift.exception.UnauthorizedException;
import gift.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Base64;
import java.util.StringTokenizer;

public class LoginMemberResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;

    public LoginMemberResolver(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Basic ")) {
            throw new UnauthorizedException("인증 헤더가 유효하지 않습니다.");
        }

        String base64Credentials = header.substring("Basic ".length());
        String credentials = new String(Base64.getDecoder().decode(base64Credentials));
        StringTokenizer tokenizer = new StringTokenizer(credentials, ":");

        String email = tokenizer.nextToken();
        String password = tokenizer.hasMoreTokens() ? tokenizer.nextToken() : "";

        return memberService.findValidMember(email, password);
    }
}
