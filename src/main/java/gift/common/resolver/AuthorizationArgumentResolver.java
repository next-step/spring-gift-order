package gift.common.resolver;

import gift.common.model.CustomAuth;
import gift.common.model.error.TokenInfo;
import gift.common.util.PasswordEncoder;
import gift.entity.User;
import gift.entity.type.Provider;
import gift.entity.type.UserRole;
import gift.service.user.UserService;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class AuthorizationArgumentResolver implements HandlerMethodArgumentResolver {
    private static final String TOKEN_ATTRIBUTE = "tokenInfo";
    private UserService userService;
    private PasswordEncoder passwordEncoder;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(CustomAuth.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory
    ) throws Exception {
        try {
            TokenInfo tokenInfo = (TokenInfo) webRequest.getAttribute(TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_REQUEST);
            return switch (tokenInfo.provider()) {
                case EMAIL -> {
                    Long userId = Long.parseLong(tokenInfo.id());
                    yield new CustomAuth(userId, tokenInfo.role(), tokenInfo.provider());
                }

                case UNKNOWN -> new CustomAuth(null, UserRole.ROLE_GUEST, Provider.UNKNOWN);
                // Oauth2, OAuth1 등 다른 인증 제공자에 대한 처리
                default -> {
                    String encodedId = passwordEncoder.encode(tokenInfo.id());
                    User user = userService.findByClientIdAndProvider(encodedId, tokenInfo.provider());
                    if (user == null) {
                        yield new CustomAuth(null, UserRole.ROLE_GUEST, Provider.UNKNOWN);
                    } else {
                        yield new CustomAuth(user.getId(), tokenInfo.role(), tokenInfo.provider());
                    }
                }
            };
        } catch (ClassCastException e) {
            // 토큰 정보가 잘못된 형식인 경우, 예외 처리
            return new CustomAuth(null, UserRole.ROLE_GUEST, Provider.UNKNOWN);
        } catch (Exception e) {
            throw new RuntimeException("예기치 못한 오류가 발생했습니다.: " + e.getMessage(), e);
        }
    }
}
