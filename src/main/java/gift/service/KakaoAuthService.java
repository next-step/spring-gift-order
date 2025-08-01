package gift.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.client.KakaoClient;
import gift.dto.kakao.KakaoMessageDto;
import gift.dto.kakao.KakaoTokenResponse;
import gift.dto.kakao.KakaoUserInfoResponse;
import gift.entity.Option;
import gift.entity.Order;
import gift.exception.KakaoMessageSendException;
import gift.exception.KakaoTokenException;
import gift.exception.KakaoUserInfoException;
import gift.exception.ProductNotFoundException;
import gift.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Map;

@Service
public class KakaoAuthService {

    private final KakaoClient kakaoClient;
    private final String clientId;
    private final String redirectUri;
    private final ObjectMapper objectMapper;
    private final OptionRepository optionRepository;

    public KakaoAuthService(
            KakaoClient kakaoClient,
            @Value("${kakao.client.id}") String clientId,
            @Value("${kakao.redirect.uri}") String redirectUri,
            OptionRepository optionRepository,
            ObjectMapper objectMapper
    ) {
        this.kakaoClient = kakaoClient;
        this.clientId = clientId;
        this.redirectUri = redirectUri;
        this.optionRepository = optionRepository;
        this.objectMapper = objectMapper;
    }

    public String getAccessToken(String code) {
        KakaoTokenResponse response = kakaoClient.getKakaoToken(code, clientId, redirectUri);

        if (response == null) {
            throw new KakaoTokenException("카카오 토큰을 발급받는데 실패했습니다.");
        }
        return response.accessToken();
    }

    public KakaoUserInfoResponse getUserInfo(String accessToken) {
        KakaoUserInfoResponse response = kakaoClient.fetchUserInfo(accessToken);

        if (response == null) {
            throw new KakaoUserInfoException("카카오 사용자 정보를 가져오는데 실패했습니다.");
        }
        return response;
    }

    public void sendMessageToMe(String accessToken, Order order) {
        try {
            Option option = optionRepository.findById(order.getOptionId())
                    .orElseThrow(() -> new ProductNotFoundException("해당 ID의 옵션을 찾을 수 없습니다: " + order.getOptionId()));

            String templateJson = KakaoMessageDto.toTemplateObject(order, option, this.objectMapper);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("template_object", templateJson);

            kakaoClient.sendKakaoTalkMessage(accessToken, body);
        } catch (Exception e) {
            throw new KakaoMessageSendException("카카오톡 메시지 전송에 실패했습니다.", e);
        }
    }
}
