package gift.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoTokenResponseDto;
import gift.jwt.JwtUtil;
import gift.repository.MemberRepository;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class KakaoAuthController {

  private static final Logger logger = LoggerFactory.getLogger(KakaoAuthController.class);
  private final MemberRepository memberRepository;
  private final JwtUtil jwtUtil;

  @Value("${kakao-rest-api-key}")
  private String client_id;

  @Value("${ec2PublicIp}")
  private String ec2PublicIp;

  public KakaoAuthController(MemberRepository memberRepository, JwtUtil jwtUtil) {
    this.memberRepository = memberRepository;
    this.jwtUtil = jwtUtil;
  }

  @GetMapping("/")
  public ResponseEntity<String> kakaoCallback(
      @RequestParam("code") String code,
      @CookieValue(name = "Authorization", required = false) String jwtToken) {

    logger.info("발급받은 인가코드 : " + code);
    logger.info("JWT 토큰 : " + jwtToken);

    String url = "https://kauth.kakao.com/oauth/token";

    var headers = new HttpHeaders();
    headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE);
    var body = new LinkedMultiValueMap<String, String>();
    body.add("grant_type", "authorization_code");
    body.add("client_id", client_id);
    body.add("redirect_uri", "http://" + ec2PublicIp + ":8080");
    body.add("code", code);
    var request = new RequestEntity<>(body, headers, HttpMethod.POST, URI.create(url));

    RestTemplate restTemplate = new RestTemplate();
    ResponseEntity<String> response = restTemplate.exchange(request, String.class);

    KakaoTokenResponseDto tokenDto;
    // Response JSON을 DTO로 변환
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      tokenDto = objectMapper.readValue(response.getBody(),
          KakaoTokenResponseDto.class);
    } catch (JsonProcessingException e) {
      logger.info("JSON필드와 제대로 매칭되지 않습니다");
      throw new RuntimeException(e);
    }

    String accessToken = tokenDto.getAccessToken();
    HttpHeaders kakaoTokenAuthHeaders = new HttpHeaders();
    kakaoTokenAuthHeaders.add("Authorization", "Bearer " + accessToken);
    RequestEntity<Void> infoRequest = new RequestEntity<>(kakaoTokenAuthHeaders, HttpMethod.GET,
        URI.create("https://kapi.kakao.com/v1/user/access_token_info"));
    ResponseEntity<String> infoResponse = restTemplate.exchange(infoRequest, String.class);

    Long kakaoId;
    try {
      JsonNode json = new ObjectMapper().readTree(infoResponse.getBody());
      kakaoId = json.get("id").asLong();
    } catch (Exception e) {
      throw new RuntimeException("카카오 id 추출 실패");
    }

    logger.info("kakaoId : " + kakaoId);
    String email = jwtUtil.getEmailFromToken(jwtToken);

    memberRepository.findByEmail(email).ifPresent(member -> {
      member.setKakaoId(kakaoId);
      memberRepository.save(member);
      logger.info("{}에 카카오톡 ID {} 저장 완료", email, kakaoId);
    });

    // DTO를 기반으로 Header에 필드별로 세팅
    HttpHeaders responseHeaders = new HttpHeaders();
    responseHeaders.add("Authorization", "Bearer " + jwtToken); // 기존 JWT
    responseHeaders.add("Kakao-AccessToken",
        "Kakao " + tokenDto.getAccessToken()); // 카카오 Access Token
    responseHeaders.add("TokenType", tokenDto.getTokenType());
    responseHeaders.add("Refresh-Token", tokenDto.getRefreshToken());
    responseHeaders.add("Expires-In", String.valueOf(tokenDto.getExpiresIn()));
    responseHeaders.add("scope", tokenDto.getScope());
    responseHeaders.add("refresh_token_expires_in",
        String.valueOf(tokenDto.getRefreshTokenExpiresIn()));

    return new ResponseEntity<>("정상적으로 토큰이 발급되었습니다!", responseHeaders, HttpStatus.CREATED);
  }
}
