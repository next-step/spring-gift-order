package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.domain.Order;
import gift.domain.ProductOption;
import gift.dto.KakaoMessageResponse;
import gift.dto.KakaoTextResponse;
import gift.repository.ProductOptionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KakaoMessageService {

    private static final Logger log = LoggerFactory.getLogger(KakaoMessageService.class);
    private static final String KAKAO_SEND_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final int KAKAO_SUCCESS_CODE = 0;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ProductOptionRepository productOptionRepository;


    public KakaoMessageService(ProductOptionRepository productOptionRepository) {
        this.productOptionRepository = productOptionRepository;
    }

    public void sendOrderMessageToMe(String accessToken, Order order) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("유효한 카카오 access token이 필요합니다.");
        }

        ProductOption option = productOptionRepository.findById(order.getOptionId())
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션이 존재하지 않습니다."));

        String messageText = """
            주문이 완료되었어요!
            - 옵션: %s
            - 수량: %d개
            - 메시지: %s
            """.formatted(option.getName(), order.getQuantity(), order.getMessage());

        KakaoTextResponse template = new KakaoTextResponse(
                messageText,
                "http://localhost:8080/api/orders",
                "http://localhost:8080/api/orders",
                "주문 확인하기"
        );

        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("템플릿 직렬화 실패", e);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("template_object", templateJson);

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(params, headers);

        try {
            ResponseEntity<KakaoMessageResponse> response = restTemplate.postForEntity(
                    KAKAO_SEND_URL,
                    entity,
                    KakaoMessageResponse.class
            );
            KakaoMessageResponse body = response.getBody();

            if (body == null || body.getResultCode() != KAKAO_SUCCESS_CODE) {
                int code = (body != null) ? body.getResultCode() : -1;
                throw new IllegalStateException("카카오 응답 실패: resultCode=" + code);
            }

            log.info("카카오톡 메시지 전송 성공: resultCode={}, templateId={}",
                    body.getResultCode(), body.getTemplateId());

        } catch (Exception e) {
            log.error("카카오톡 메시지 전송 실패", e);
            throw new RuntimeException("카카오톡 메시지 전송 실패: " + e.getMessage(), e);
        }
    }
}