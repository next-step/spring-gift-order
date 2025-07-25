package gift.resolver;

import gift.Entity.Member;
import gift.Jwt.JwtUtil;
import gift.annotation.LoginMember;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtUtil jwtUtil;

    public LoginMemberArgumentResolver(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        //@LoginMember 어노테이션이 붙어있는가?
        boolean isLoginMemberAnnotation = parameter.hasParameterAnnotation(LoginMember.class);
        // 타입이 Member이거나 그 하위 클래스인가?
        boolean isMemberClass = parameter.getParameterType().isAssignableFrom(Member.class);
        // 모두 만족하면 resolveArgument()를 호출하게됨
        return isLoginMemberAnnotation && isMemberClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest httpServletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        if (httpServletRequest == null) return null;

        String token = null;
        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("Authorization".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        if (token != null) {
            try {
                Claims claims = jwtUtil.parseToken(token);

                Member member = new Member();
                member.setNickname(claims.getSubject());
                member.setName((String) claims.get("name"));
                member.setEmail((String) claims.get("email"));
                member.setAddress((String) claims.get("address"));
                member.setRole((String) claims.get("role"));

                return member;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }
}
