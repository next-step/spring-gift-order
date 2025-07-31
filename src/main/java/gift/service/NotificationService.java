package gift.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.dto.kakao.template.ButtonDto;
import gift.dto.kakao.template.CommerceContentDto;
import gift.dto.kakao.template.CommerceDetailsDto;
import gift.dto.kakao.template.KakaoCommerceTemplateDto;
import gift.dto.kakao.template.LinkDto;
import gift.entity.Order;
import gift.entity.Product;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final KakaoClient kakaoClient;
    private final ObjectMapper objectMapper;

    public NotificationService(KakaoClient kakaoClient, ObjectMapper objectMapper) {
        this.kakaoClient = kakaoClient;
        this.objectMapper = objectMapper;
    }


    public void sendOrderCompletionNotification(Order order, String kakaoAccessToken) {
        KakaoCommerceTemplateDto templateDto = createKakaoCommerceTemplate(order);
        try {
            String templateJson = objectMapper.writeValueAsString(templateDto);
            kakaoClient.sendKakaoTalkMessage(kakaoAccessToken, templateJson);
        } catch (JsonProcessingException e) {
            log.error("카카오 메시지 템플릿 직렬화에 실패했습니다.", e);
        }
    }

    private KakaoCommerceTemplateDto createKakaoCommerceTemplate(Order order) {
        Product product = order.getOption().getProduct();

        LinkDto productLink = new LinkDto("http://localhost:8080", "http://localhost:8080");
        LinkDto buttonLink = new LinkDto("http://localhost:8080/wishes",
                "http://localhost:8080/wishes");

        CommerceContentDto content = new CommerceContentDto(
                "주문이 성공적으로 완료되었습니다.",
                product.getImageUrl(),
                productLink
        );

        CommerceDetailsDto commerceDetails = new CommerceDetailsDto(
                product.getName() + " (" + order.getOption().getName() + ")",
                product.getPrice() * order.getQuantity()
        );

        ButtonDto viewOrderButton = new ButtonDto("주문 상세 보기", buttonLink);

        return new KakaoCommerceTemplateDto(
                "commerce",
                content,
                commerceDetails,
                List.of(viewOrderButton)
        );
    }
}