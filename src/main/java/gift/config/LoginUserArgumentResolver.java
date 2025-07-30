package gift.config;

import gift.dto.kakao.LoginUserInfo;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    public LoginUserArgumentResolver(JwtProvider jwtProvider) {
        this.jwtProvider = jwtProvider;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(gift.config.LoginUser.class) &&
                parameter.getParameterType().equals(LoginUserInfo.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        String bearerToken = webRequest.getHeader("Authorization");

        String token = bearerToken.substring(7); // "Bearer " 제거
        Long id = jwtProvider.getId(token);
        String email = jwtProvider.getEmail(token);
        String role = jwtProvider.getRole(token);
        String accessToken =jwtProvider.getKakaoAccessToken(token);

        return new LoginUserInfo(id, email, role,accessToken);
    }
}

