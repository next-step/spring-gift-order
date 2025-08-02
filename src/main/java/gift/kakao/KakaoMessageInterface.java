package gift.kakao;

import gift.dto.kakaoApi.KakaoUserInfoResponse;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange()
public interface KakaoMessageInterface {

    @PostExchange(value = "${https://kapi.kakao.com/v2/api/talk/memo/default/send}",
        contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    ResponseEntity<Map<String, Object>> sendMessageToMySelf(
        @RequestHeader("Authorization") String token,
        @RequestParam("template_object") String request
    );

    @GetExchange(value = "https://kapi.kakao.com/v2/user/me")
    ResponseEntity<KakaoUserInfoResponse> getUserInfo(
        @RequestHeader("Authorization") String token
    );
}
