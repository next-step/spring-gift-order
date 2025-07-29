package gift.service.kakaoService;

import gift.entity.SocialUser;
import gift.service.userService.SocialUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
public class KaKaoMessageService {

    private final SocialUserService socialUserService;

    public KaKaoMessageService(SocialUserService socialUserService) {
        this.socialUserService = socialUserService;
    }

    public void sendMessage(String userEmail, String message) {
        SocialUser user = socialUserService.findByUserEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저의 카카오 엑세스 토큰이 존재하지 않습니다.");
        }

        String kakaoAccessToken = user.getKakaoAccessToken();

        String apiUrl = "https://kapi.kakao.com/v2/api/talk/memo/default/send";

        String templateJson = String.format("""
                    {
                      "object_type": "text",
                      "text": "%s",
                      "link": {
                        "web_url": "https://developers.kakao.com",
                        "mobile_web_url": "https://developers.kakao.com"
                      },
                      "button_title": "확인"
                    }
                """, message);


        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("template_object", templateJson);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setBearerAuth(kakaoAccessToken);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = new RestTemplate().postForEntity(apiUrl, request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new RuntimeException("카카오 메시지 전송 실패: " + response.getBody());
        }
    }
}
