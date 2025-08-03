package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.Order;
import gift.domain.UserKakaoToken;
import gift.repository.UserKakaoTokenRepository;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriUtils;

@Service
public class KakaoMessageServiceImpl implements KakaoMessageService {

  private final RestClient restClient;

  private final KakaoTokenService kakaoTokenService;

  private final ObjectMapper objectMapper;

  public KakaoMessageServiceImpl(RestClient.Builder restClientBuilder, KakaoTokenService kakaoTokenService, ObjectMapper objectMapper) {
    this.restClient = restClientBuilder
        .baseUrl("https://kapi.kakao.com")
        .build();
    this.kakaoTokenService = kakaoTokenService;
    this.objectMapper = objectMapper;
  }

  @Override
  @Transactional(readOnly = true)
  public void sendOrderMessage(Long memberId, Order order) {
    String accessToken = kakaoTokenService.getMemberAccessToken(memberId);

    String templateJson = createTemplateJson(order);
    String formBody = "template_object=" + UriUtils.encode(templateJson, StandardCharsets.UTF_8);

    restClient.post()
        .uri("/v2/api/talk/memo/default/send")
        .header("Authorization", "Bearer " + accessToken)
        .header("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8")
        .body(formBody)
        .retrieve()
        .toBodilessEntity();
  }

  private String createTemplateJson(Order order) {
    Map<String, Object> template = new HashMap<>();
    template.put("object_type", "text");
    template.put("text", "주문이 완료되었습니다. 옵션ID: " + order.getOptionId()
        + ", 수량: " + order.getQuantity()
        + ", 메세지: " + order.getMessage());

    Map<String, String> link = new HashMap<>();
    link.put("web_url", "https://your-service-url.com/orders");
    template.put("link", link);

    template.put("button_title", "주문 내역 확인");

    try {
      return objectMapper.writeValueAsString(template);
    } catch (JsonProcessingException e) {
      throw new RuntimeException("JSON 변환 실패", e);
    }
  }
}
