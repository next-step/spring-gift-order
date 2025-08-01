package gift.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.KakaoFeedMessageDto;
import gift.entity.ProductOption;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
public class KakaoServiceClient {
    private final ObjectMapper objectMapper;

    private final RestTemplate kakaoKapiRestTemplate;

    public KakaoServiceClient(RestTemplate kakaoKapiRestTemplate, ObjectMapper objectMapper) {
        this.kakaoKapiRestTemplate = kakaoKapiRestTemplate;
        this.objectMapper = objectMapper;
    }

    KakaoFeedMessageDto createKakaoFeedOrderMessage(ProductOption productOption, String message) {
        // 주문자 피드 메세지 생성
        List<KakaoFeedMessageDto.Item> items = List.of(new KakaoFeedMessageDto.Item(
                productOption.getOption().getName(),
                productOption.getProduct().getPrice().toString()));

        return new KakaoFeedMessageDto(
                new KakaoFeedMessageDto.Content(message),
                new KakaoFeedMessageDto.ItemContent(
                        productOption.getProduct().getImageUrl(),
                        productOption.getProduct().getName(),
                        items,
                        productOption.getProduct().getPrice().toString())
        );
    }

    public void sendFeedMessageToMe (String accessToken, ProductOption productOption, String message) {
        final String url = "/v2/api/talk/memo/default/send";
        String json;

        var headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        KakaoFeedMessageDto kakaoFeedMessageDto = createKakaoFeedOrderMessage(productOption, message);

        try {
            json = objectMapper
                    .writerWithDefaultPrettyPrinter()      // 보기 좋게 포맷팅
                    .writeValueAsString(kakaoFeedMessageDto);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        var body = new LinkedMultiValueMap<String, String>();
        body.add("template_object", json);

        var httpentity = new HttpEntity<>(body, headers);

        var response = kakaoKapiRestTemplate.exchange(url, HttpMethod.POST, httpentity, String.class);
    }
}
