package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.request.KakaoLink;
import gift.dto.request.KakaoMessageTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


@Service
public class KakaoMessageService {

    private final RestClient restClient = RestClient.create();
    private final ObjectMapper objectMapper = new ObjectMapper();


    public void sendMessageToMe(String accessToken,
                                String productName,
                                String optionName,
                                int quantity,
                                String userMessage,
                                LocalDateTime orderDateTime) {

        String messageText = formatOrderMessage(productName, optionName, quantity, userMessage, orderDateTime);

        KakaoLink link = new KakaoLink(
                "https://your-service.com",
                "https://your-service.com"
        );

        KakaoMessageTemplate template = new KakaoMessageTemplate(
                "text",
                messageText,
                link,
                "확인"
        );

        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(template);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }

    private String formatOrderMessage(String productName,
                                      String optionName,
                                      int quantity,
                                      String userMessage,
                                      LocalDateTime orderDateTime) {
        return """
                🎁 주문이 완료되었습니다!
                
                상품명: %s
                옵션: %s
                수량: %d개
                메시지: %s
                주문일: %s
                """.formatted(
                productName,
                optionName,
                quantity,
                userMessage != null && !userMessage.isBlank() ? userMessage : "-",
                orderDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
        );
    }
}
