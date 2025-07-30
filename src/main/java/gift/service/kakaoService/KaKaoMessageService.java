package gift.service.kakaoService;

import com.fasterxml.jackson.databind.ObjectMapper;
import gift.dto.kakaoDto.KakaoMessageDto;
import gift.entity.SocialUser;
import gift.service.userService.SocialUserService;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class KaKaoMessageService {

    private final SocialUserService socialUserService;
    private final RestClient restClient;

    public KaKaoMessageService(SocialUserService socialUserService, RestClient restClient) {
        this.socialUserService = socialUserService;
        this.restClient = restClient;
    }

    public void sendMessage(String userEmail, String message) {
        SocialUser user = socialUserService.findByUserEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("해당 유저의 카카오 엑세스 토큰이 존재하지 않습니다.");
        }

        String kakaoAccessToken = user.getKakaoAccessToken();
        KakaoMessageDto template = new KakaoMessageDto(message);

        try {
            restClient.post()
                    .uri("https://kapi.kakao.com/v2/api/talk/memo/default/send")
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + kakaoAccessToken)
                    .body(Map.of("template_object", template))
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        throw new RuntimeException("카카오 메시지 전송 실패 - 상태 코드: " + res.getStatusCode());
                    })
                    .toBodilessEntity();
        } catch (ResourceAccessException timeoutEx) {
            throw new RuntimeException("네트워크 문제로 카카오 메시지 전송에 실패했습니다.", timeoutEx);
        } catch (RestClientException clientEx) {
            throw new RuntimeException("클라이언트로 카카오 메시지 전송에 실패했습니다", clientEx);
        } catch (Exception e) {
            throw new RuntimeException("알 수 없는 오류로 카카오 메시지 전송에 실패했습니다.", e);
        }
    }
}
