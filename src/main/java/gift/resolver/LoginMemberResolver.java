package gift.resolver;

import gift.exception.UnauthorizedException;
import gift.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Map;

public class LoginMemberResolver implements HandlerMethodArgumentResolver {

    private final MemberService memberService;
    private final RestTemplate restTemplate = new RestTemplate();

    public LoginMemberResolver(MemberService memberService) {
        this.memberService = memberService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginMember.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {

        HttpServletRequest request = webRequest.getNativeRequest(HttpServletRequest.class);
        String header = request.getHeader("Authorization");

        if (header == null || !header.startsWith("Bearer ")) {
            throw new UnauthorizedException("인증 헤더가 유효하지 않습니다.");
        }

        String accessToken = header.substring("Bearer ".length());
        String email = fetchEmailFromKakao(accessToken);

        return memberService.findByEmailOrRegister(email);
    }

    private String fetchEmailFromKakao(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        HttpEntity<Void> request = new HttpEntity<>(headers);
        String url = "https://kapi.kakao.com/v2/user/me";

        try {
            ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.GET, request, Map.class);
            Map<String, Object> kakaoAccount = (Map<String, Object>) response.getBody().get("kakao_account");
            if (kakaoAccount == null || kakaoAccount.get("email") == null) {
                throw new UnauthorizedException("카카오 계정에 이메일 정보가 없습니다.");
                // return "asdf@kakao.com";
            }
            return kakaoAccount.get("email").toString();

        } catch (HttpClientErrorException e) {
            throw new UnauthorizedException("카카오 토큰이 유효하지 않습니다.");
        }
    }
}
