package gift.service;

import gift.entity.Member;
import gift.entity.Order;
import gift.repository.ProductRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageService {

    private final RestTemplate restTemplate;
    private final ProductRepository productRepository;

    public KakaoMessageService(RestTemplate restTemplate, ProductRepository productRepository) {
        this.restTemplate = restTemplate;
        this.productRepository = productRepository;
    }

    public void sendMessage(Member member, Order order) {
        String url = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(member.getKakaoAccessToken());
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        var product = order.getProduct();
        var message = order.getMessage();

        String templateObject = """
{
  "object_type": "feed",
  "content": {
    "title": "%s",
    "description": "%s",
    "image_url": "%s",
    "image_width": 640,
    "image_height": 640,
    "link": {
      "web_url": "https://localhost:8080",
      "mobile_web_url": "https://localhost:8080"
    }
  },
  "buttons": [
    {
      "title": "자세히 보기",
      "link": {
        "web_url": "https://localhost:8080",
        "mobile_web_url": "https://localhost:8080"
      }
    }
  ]
}
""".formatted(
                escapeJson(product.getName()),
                escapeJson(order.getMessage()),
                product.getImageUrl()
        );


        var body = new LinkedMultiValueMap<String, String>();
        body.add("template_object", templateObject);

        HttpEntity<LinkedMultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        restTemplate.postForEntity(url, request, String.class);
    }

    private String escapeJson(String text) {
        return text.replace("\"", "\\\"")
                .replace("\n", "\\n");
    }
}
