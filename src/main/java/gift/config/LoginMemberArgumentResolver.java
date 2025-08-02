package gift.config;

import gift.exception.notfound.MemberNotFoundException;
import gift.repository.member.MemberJpaRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final String secretKey = "Yn2kjibddFAWtnPJ2AFlL8WXmohJMCvigQggaEypa5E=";
  private final String AuthorizationCategory = "Bearer";
  private final MemberJpaRepository repository;

  public LoginMemberArgumentResolver(MemberJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginMember.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

    String authHeader = webRequest.getHeader("Authorization");

    if (authHeader == null || !authHeader.startsWith(AuthorizationCategory + " ")) {
      throw new IllegalStateException("no authorization ");
    }

    String token = authHeader.substring(AuthorizationCategory.length() + 1);

    Claims claims = Jwts.parser()
        .setSigningKey(secretKey.getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody();
    Long memberId = Long.parseLong(claims.getSubject());
    return repository.findById(memberId)
        .orElseThrow(() -> new MemberNotFoundException("토큰에 해당하는 멤버가 없습니다."));
  }
}
