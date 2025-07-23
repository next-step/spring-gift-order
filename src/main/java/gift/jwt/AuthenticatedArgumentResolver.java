package gift.jwt;

import gift.dto.LoginMember;
import gift.entity.Member;
import gift.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthenticatedArgumentResolver implements HandlerMethodArgumentResolver {

    private final MemberRepository memberRepository;

    public AuthenticatedArgumentResolver(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Authenticated.class)
                && parameter.getParameterType().equals(Member.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        Object loginMemberAttr = request.getAttribute("loginMember");

        if (!(loginMemberAttr instanceof LoginMember loginMember)) {
            throw new IllegalStateException("인증된 사용자가 없습니다.");
        }

        return memberRepository.findById(loginMember.getId())
                .orElseThrow(() -> new IllegalStateException("회원 정보를 찾을 수 없습니다."));
    }
}
