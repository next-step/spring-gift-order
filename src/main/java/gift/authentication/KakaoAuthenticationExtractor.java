package gift.authentication;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.Controller.KakaoAuthController;
import gift.model.Member;
import gift.repository.MemberRepository;
import jakarta.servlet.ServletException;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class KakaoAuthenticationExtractor implements AuthenticationExtractor {

  private static final Logger logger = LoggerFactory.getLogger(KakaoAuthController.class);
  private final MemberRepository memberRepository;
  private final RestTemplate restTemplate;

  public KakaoAuthenticationExtractor(MemberRepository memberRepository) {
    this.memberRepository = memberRepository;
    this.restTemplate = new RestTemplate();
  }

  @Override
  public boolean supports(String header) {
    return header != null && header.startsWith("Kakao ");
  }

  @Override
  public Member extract(String header) throws ServletException {
    String kakaoAccessToken = header.substring(6); // "Kakao " 제거

    String url = "https://kapi.kakao.com/v1/user/access_token_info";
    var headers = new HttpHeaders();
    headers.add("Authorization", "Bearer " + kakaoAccessToken);
    var request = new RequestEntity<>(headers, HttpMethod.GET, URI.create(url));

    try {
      ResponseEntity<String> response = restTemplate.exchange(request, String.class);

      ObjectMapper objectMapper = new ObjectMapper();
      JsonNode json = objectMapper.readTree(response.getBody());

      if (json.has("id")) {
        Long kakaoId = json.get("id").asLong();
        logger.info("✅ 유효한 카카오 AccessToken입니다. 사용자 ID: {}", kakaoId);

        return memberRepository.findByKakaoId(kakaoId)
            .orElseThrow(() -> new SecurityException("해당 kakaoId의 회원이 존재하지 않습니다"));
      }

      if (json.has("code") && json.get("code").asInt() == -401) {
        throw new SecurityException("❌ 유효하지 않은 AccessToken: " + json.get("msg").asText());
      }

      throw new SecurityException("❓ 예기치 않은 응답 형식: " + response.getBody());

    } catch (SecurityException e) {
      throw e;
    } catch (Exception e) {
      logger.error("🔥 카카오 AccessToken 검증 중 오류 발생", e);
      throw new ServletException("카카오 인증 처리 중 오류 발생", e);
    }
  }

}
