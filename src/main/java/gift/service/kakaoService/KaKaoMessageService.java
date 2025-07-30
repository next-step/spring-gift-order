package gift.service.kakaoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakaoDto.KakaoMessageDto;
import gift.entity.SocialUser;
import gift.service.userService.SocialUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
public class KaKaoMessageService {

    private final SocialUserService socialUserService;
    private final ObjectMapper objectMapper;
    private final RestClient restClient;

    public KaKaoMessageService(SocialUserService socialUserService, ObjectMapper objectMapper, RestClient restClient) {
        this.socialUserService = socialUserService;
        this.objectMapper = objectMapper;
        this.restClient = restClient;
    }

    public void sendMessage(String userEmail, String message) {
        SocialUser user = socialUserService.findByUserEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저의 카카오 엑세스 토큰이 존재하지 않습니다.");
        }

        String kakaoAccessToken = user.getKakaoAccessToken();

        KakaoMessageDto template = new KakaoMessageDto(message);
        String templateJson;
        try {
            templateJson = objectMapper.writeValueAsString(template);
        } catch (Exception e) {
            throw new RuntimeException("KakaoMessageDto -> JSON 직렬화에 문제 발생: ", e);
        }

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        restClient.post()
                .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new RuntimeException("카카오 메시지 전송 실패: " + res.getStatusCode());
                })
                .toBodilessEntity();
    }
}
