package gift.util;

import gift.authentication.JwtAuthenticationExtractor;
import gift.authentication.KakaoAuthenticationExtractor;
import gift.exception.UnsupportedAuthException;
import gift.model.Member;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {

  private final JwtAuthenticationExtractor jwtAuthenticationExtractor;
  private final KakaoAuthenticationExtractor kakaoAuthenticationExtractor;

  public LoginMemberArgumentResolver(JwtAuthenticationExtractor jwtAuthenticationExtractor,
      KakaoAuthenticationExtractor kakaoAuthenticationExtractor) {
    this.jwtAuthenticationExtractor = jwtAuthenticationExtractor;
    this.kakaoAuthenticationExtractor = kakaoAuthenticationExtractor;
  }


  @Override
  public boolean supportsParameter(MethodParameter parameter) {
    return parameter.hasParameterAnnotation(LoginMember.class) &&
        parameter.getParameterType().equals(Member.class);
  }

  @Override
  public Object resolveArgument(MethodParameter parameter,
      ModelAndViewContainer mavContainer,
      NativeWebRequest webRequest,
      WebDataBinderFactory binderFactory) throws Exception {

    String jwtHeader = webRequest.getHeader("Authorization"); // 필수
    String kakaoHeader = webRequest.getHeader("Kakao-AccessToken"); // 선택
    // 나중에 보조 토큰(구글,네이버 확장 가능)

    Member member = null;

    // ✅ 1. JWT는 반드시 있어야함 -> 없으면 인증오류
    if (jwtHeader == null || jwtHeader.isBlank()) {
      throw new UnsupportedAuthException("JWT 토큰이 누락되었습니다");
    }

    boolean jwtValidated = false;
    if (jwtAuthenticationExtractor.supports(jwtHeader)) {
      member = jwtAuthenticationExtractor.extract(jwtHeader);
      jwtValidated = true;
    }

    if (!jwtValidated) {
      throw new UnsupportedAuthException("JWT 인증에 실패했습니다");
    }

    // ✅ 2. 카카오톡 토큰 인증 수행
    if (kakaoHeader != null && !kakaoHeader.isBlank()) {
      boolean kakaoValidated = false;
      if (kakaoAuthenticationExtractor.supports(kakaoHeader)){
        kakaoAuthenticationExtractor.extract(kakaoHeader); // 실패하면 예외 발생
        kakaoValidated = true;
      }

      if (!kakaoValidated) {
        throw new UnsupportedAuthException("Kakao 인증에 실패했습니다");
      }
    }
    return member; // LoginMember로 주입될 객체
  }

}
