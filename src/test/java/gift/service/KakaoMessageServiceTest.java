package gift.service;

import gift.common.exception.KakaoMessageSendException;
import gift.service.api.KakaoMessageService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(value = KakaoMessageService.class)
public class KakaoMessageServiceTest {

    @Autowired
    private KakaoMessageService kakaoMessageService;

    @Autowired
    private MockRestServiceServer mockServer;

    @Test
    @DisplayName("카카오 메세지 전송에서 result_code가 0으로 반환되면 올바르게 메세지 전송 완료")
    void test1() {
        String accessToken = "저는액세스토큰입니다.";
        String message = "생일 축하합니다!";

        String responseBody = """
                {
                  "result_code": 0
                }
                """;

        this.mockServer.expect(requestTo("https://kapi.kakao.com/v2/api/talk/memo/default/send"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        kakaoMessageService.sendOrderCompleteMessage(accessToken, message);
    }

    @Test
    @DisplayName("카카오 메세지 전송에서 result_code가 0으로 반환되지 않을 경우 KakaoMessageSendException 반환")
    void test2() {
        String accessToken = "저는액세스토큰입니다.";
        String message = "생일 축하합니다!";

        String responseBody = """
                {
                  "result_code": -1
                }
                """;

        this.mockServer.expect(requestTo("https://kapi.kakao.com/v2/api/talk/memo/default/send"))
                .andRespond(withSuccess(responseBody, MediaType.APPLICATION_JSON));

        assertThatThrownBy(() -> kakaoMessageService.sendOrderCompleteMessage(accessToken, message)).isInstanceOf(KakaoMessageSendException.class);
    }

    @Test
    @DisplayName("카카오 메세지 전송에서 400번대 에러가 떨어지는 경우 KakaoMessageSendException 반환")
    void test3() {
        String accessToken = "저는액세스토큰입니다.";
        String message = "생일 축하합니다!";

        this.mockServer.expect(requestTo("https://kapi.kakao.com/v2/api/talk/memo/default/send"))
                .andRespond(withBadRequest());

        assertThatThrownBy(() -> kakaoMessageService.sendOrderCompleteMessage(accessToken, message)).isInstanceOf(KakaoMessageSendException.class);
    }
}
