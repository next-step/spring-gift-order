package gift.auth;

import gift.exception.AuthenticationException;
import gift.repository.MemberRepository;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    public LoginMemberArgumentResolver(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Object memberIdObject = webRequest.getAttribute("memberId", NativeWebRequest.SCOPE_REQUEST);

        if (memberIdObject == null) {
            throw new AuthenticationException("인증되지 않은 사용자입니다.");
        }

        Long memberId = (Long) memberIdObject;
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new AuthenticationException("사용자 정보를 찾을 수 없습니다."));
    }
}
